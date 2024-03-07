package org.vedant.service;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.vedant.entity.LinkData;
import org.vedant.entity.StaxPost;
import org.vedant.entity.WikiPost;
import org.vedant.repository.UnixStackRepository;
import org.vedant.repository.UnixWikiRepository;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;


@Service
@Log
public class DataIndexingService {

    public static final String USER_DIR = "user.dir";
    public static final String ROW = "row";
    public static final String DOC = "doc";
    @Autowired
    UnixStackRepository unixStackRepository;

    @Autowired
    UnixWikiRepository unixWikiRepository;

    @Value("${location.destination.directory}")
    String destinationDirectory;
    @Value("${location.destination.staxFile}")
    String destinationStaxFile;
    @Value("${location.destination.wikiFile}")
    String destinationWikiFile;

    @EventListener(ApplicationReadyEvent.class)
    public void updateStackData(){
        try {
            File xmlFile = new File(System.getProperty(USER_DIR),destinationDirectory + destinationStaxFile);
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(new FileInputStream(xmlFile));

            List<StaxPost> staxPostList = new ArrayList<>();
            int count =0;
            // Iterate over each <row> element
            while (xmlStreamReader.hasNext()){
                int event = xmlStreamReader.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    String elementName = xmlStreamReader.getLocalName();
                    if (ROW.equals(elementName)) {
                        StaxPost currentStaxPost = new StaxPost();
                        unixPostMapper(currentStaxPost, xmlStreamReader);
                        staxPostList.add(currentStaxPost);
                        count++;
                    }
                }
                if(count>5000){
                    unixStackRepository.saveAll(staxPostList);
                    staxPostList.clear();
                    count = 0;
                }
            }
        } catch (Exception e) {
            log.throwing("DataIndexingService", "updateSolrWithXmlData", e);
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void updateWikiData(){
        try {
            File xmlFile = new File(System.getProperty(USER_DIR),destinationDirectory + destinationWikiFile);
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(new FileInputStream(xmlFile));

            List<WikiPost> wikiPostList = new ArrayList<>();
            int count =0;
            WikiPost wikiPost = null;
            while (xmlStreamReader.hasNext()){
                int event = xmlStreamReader.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    String elementName = xmlStreamReader.getLocalName();
                    if (DOC.equals(elementName)) {
                        WikiPost newWikiPost = new WikiPost();
                        newWikiPost.setLinks(new ArrayList<>());
                        wikiPostList.add(newWikiPost);
                        wikiPost = newWikiPost;
                        count++;
                    } else{
                        wikiPostMapper(wikiPost, elementName, xmlStreamReader);
                    }
                }
                if(count>5000){
                    unixWikiRepository.saveAll(wikiPostList);
                    wikiPostList.clear();
                    count = 0;
                }
            }
        } catch (Exception e) {
            log.throwing("DataIndexingService", "updateSolrWithXmlData", e);
        }
    }

    private static void wikiPostMapper(WikiPost wikiPost, String elementName, XMLStreamReader xmlStreamReader) throws XMLStreamException {
        if(wikiPost != null){
            switch (elementName) {
                case "title":
                    wikiPost.setTitle(xmlStreamReader.getElementText());
                    break;
                case "url":
                    wikiPost.setUrl(xmlStreamReader.getElementText());
                    break;
                case "abstract":
                    wikiPost.setAbstractText(xmlStreamReader.getElementText());
                    break;
                case "sublink":
                    wikiPost.getLinks().add(new LinkData());
                    break;
                case "anchor":
                    wikiPost.getLinks().getLast().setAnchor(xmlStreamReader.getElementText());
                    break;
                case "link":
                    wikiPost.getLinks().getLast().setLink(xmlStreamReader.getElementText());
                    break;
            }
        }
    }

    private static void unixPostMapper(StaxPost staxPost, XMLStreamReader rowElement) {
        staxPost.setId(parseIntSafe(rowElement.getAttributeValue(null,"Id")));
        staxPost.setPostTypeId(parseIntSafe(rowElement.getAttributeValue(null,"PostTypeId")));
        staxPost.setAcceptedAnswerId(parseIntSafe(rowElement.getAttributeValue(null,"AcceptedAnswerId")));
        staxPost.setCreationDate(rowElement.getAttributeValue(null,"CreationDate"));
        staxPost.setScore(parseIntSafe(rowElement.getAttributeValue(null,"Score")));
        staxPost.setViewCount(parseIntSafe(rowElement.getAttributeValue(null,"ViewCount")));
        staxPost.setBody(rowElement.getAttributeValue(null,"Body"));
        staxPost.setOwnerUserId(parseIntSafe(rowElement.getAttributeValue(null,"OwnerUserId")));
        staxPost.setLastEditorUserId(parseIntSafe(rowElement.getAttributeValue(null,"LastEditorUserId")));
        staxPost.setLastEditDate(rowElement.getAttributeValue(null,"LastEditDate"));
        staxPost.setLastActivityDate(rowElement.getAttributeValue(null,"LastActivityDate"));
        staxPost.setTitle(rowElement.getAttributeValue(null,"Title"));
        staxPost.setTags(rowElement.getAttributeValue(null,"Tags"));
        staxPost.setAnswerCount(parseIntSafe(rowElement.getAttributeValue(null,"AnswerCount")));
        staxPost.setCommentCount(parseIntSafe(rowElement.getAttributeValue(null,"CommentCount")));
        staxPost.setContentLicense(rowElement.getAttributeValue(null,"ContentLicense"));
    }
    public static int parseIntSafe(String var) {
        if (var != null) {
            try {
                return Integer.parseInt(var);
            } catch (NumberFormatException e) {
                // Handle the case when var is not a valid integer
                System.err.println("Error parsing integer: " + e.getMessage());
            }
        }
        // Return 0 for null or invalid input
        return 0;
    }
}
