package org.vedant.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.java.Log;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

@Service
@Log
public class AsyncDownloadService {

    public static final String POSTS_XML = "posts.xml";

    private static final int BUFFER_SIZE = 1024;
    @Autowired
    DataIndexingService dataIndexingService;
    RestTemplate restTemplate;

    @Value("${download.stax.url}")
    String stackUrl;
    @Value("${download.wiki.url}")
    String wikiUrl;
    @Value("${location.destination.directory}")
    String destinationDirectory;
    @Value("${location.destination.staxFile}")
    String destinationStaxFile;
    @Value("${location.destination.wikiFile}")
    String destinationWikiFile;

    @Value("${location.source.staxFile}")
    String sourceStaxFile;
    @Value("${location.source.wikiFile}")
    String sourceWikiFile;

    @PostConstruct
    public void setup(){
        restTemplate = new RestTemplate();
    }

    @Async
    public void checkStackExchange()  {
        try {
            ResponseEntity<byte[]> response = restTemplate.getForEntity(new URI(stackUrl), byte[].class);

            // Check if the response is successful (status code 2xx)
            if (response.getStatusCode().is2xxSuccessful()) {
                saveToFile(response.getBody(), destinationDirectory, sourceStaxFile);
                extract7z(destinationDirectory+sourceStaxFile, destinationDirectory+destinationStaxFile);
                dataIndexingService.updateStackData();
            } else {
                throw new RuntimeException("Failed to download file. HTTP status code: " + response.getStatusCode().value());
            }
        } catch (Exception ex){
            log.throwing("AsyncDownloadService", "checkStackExchange", ex);
        }
    }

    private void extract7z(String source, String destination) {
        try ( SevenZFile sevenZFile = SevenZFile.builder().setFile(System.getProperty("user.dir")+source).get()) {
            for (SevenZArchiveEntry sevenZArchiveEntry : sevenZFile.getEntries()) {
                if (Objects.equals(sevenZArchiveEntry.getName(), POSTS_XML)) {
                    byte[] buffer;
                    File file = new File(System.getProperty("user.dir") + destination);
                    if(!file.exists()){
                        file.createNewFile();
                    }
                    try (OutputStream outputStream = new FileOutputStream(System.getProperty("user.dir")+destination)) {
                        InputStream is = sevenZFile.getInputStream(sevenZArchiveEntry);
                        while ((buffer = is.readNBytes(BUFFER_SIZE)) != null) {
                            outputStream.write(buffer, 0, buffer.length);
                            if(buffer.length < BUFFER_SIZE){
                                break;
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Async
    public void checkWikipedia()  {
        try {
            ResponseEntity<byte[]> response = restTemplate.getForEntity(new URI(wikiUrl), byte[].class);

            // Check if the response is successful (status code 2xx)
            if (response.getStatusCode().is2xxSuccessful()) {
                saveToFile(response.getBody(), destinationDirectory, sourceWikiFile);
                extractGz(destinationDirectory+sourceWikiFile, destinationDirectory+destinationWikiFile);
                dataIndexingService.updateWikiData();
            } else {
                throw new RuntimeException("Failed to download file. HTTP status code: " + response.getStatusCode().value());
            }
        } catch (Exception ex){
            log.throwing("AsyncDownloadService", "checkStackExchange", ex);
        }
    }

    private void extractGz(String inputFilePath, String outputFilePath) {
        try (FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+inputFilePath);
             GZIPInputStream gzis = new GZIPInputStream(fis);
             FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir")+outputFilePath)) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveToFile(byte[] body, String destinationFilePath, String fileName) throws IOException {
        Path path = Paths.get(System.getProperty("user.dir")+destinationFilePath, fileName);
        File file = path.toFile();
        if(!file.exists()){
            file.createNewFile();
        }
        try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
            fos.write(body);
        } catch (IOException e) {
            throw new RuntimeException("Error saving file: " + e.getMessage(), e);
        }
    }
}
