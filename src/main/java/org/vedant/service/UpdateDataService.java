package org.vedant.service;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Log
public class UpdateDataService {

    @Autowired
    AsyncDownloadService asyncDownloadService;
    @Scheduled(cron = "0 0 0 1 */1 ?")
    public void monthlyUpdate(){
        log.info("Running data update");
        asyncDownloadService.checkStackExchange();
        asyncDownloadService.checkWikipedia();
        log.info("Data updation completed");
    }
}
