package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.vedant.service.AsyncDownloadService;
import org.vedant.service.DataIndexingService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CronJobTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AsyncDownloadService asyncDownloadService;

    @Mock
    private DataIndexingService dataIndexingService;

    @BeforeEach
    public void setup(){
        ReflectionTestUtils.setField(asyncDownloadService, "stackUrl", "https://archive.org/download/stackexchange/unix.stackexchange.com.7z");
        ReflectionTestUtils.setField(asyncDownloadService, "wikiUrl", "https://dumps.wikimedia.org/enwiki/latest/enwiki-latest-abstract.xml.gz");
        ReflectionTestUtils.setField(asyncDownloadService, "destinationDirectory", "/downloads/");
        ReflectionTestUtils.setField(asyncDownloadService, "destinationStaxFile", "unix_stackexchange.xml");
        ReflectionTestUtils.setField(asyncDownloadService, "destinationWikiFile", "unix_wiki.xml");
        ReflectionTestUtils.setField(asyncDownloadService, "sourceStaxFile", "unix_stackexchange.7z");
        ReflectionTestUtils.setField(asyncDownloadService, "sourceWikiFile", "test_wiki.xml.gz");

    }

    @Test
    void testCheckStackExchangeSuccess() throws Exception {
        // Mocking successful response from Stack Exchange
        File file = new File(System.getProperty("user.dir")+"/testFile/test_stax.7z");
        byte[] fakeResponse = convertFileToByteArray(file);
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(fakeResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(any(),  any())).thenReturn(responseEntity);

        // Testing the method
        asyncDownloadService.checkStackExchange();

        // Add assertions as needed
    }

    @Test
    void testCheckWikipediaSuccess() throws Exception {
        File file = new File(System.getProperty("user.dir")+"/testFile/test_wiki.xml.gz");
        byte[] fakeResponse = convertFileToByteArray(file);
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(fakeResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(any(),  any())).thenReturn(responseEntity);

        // Testing the method
        asyncDownloadService.checkWikipedia();

        // Add assertions as needed
    }



    public static byte[] convertFileToByteArray(File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            return byteArrayOutputStream.toByteArray();
        }
    }
}