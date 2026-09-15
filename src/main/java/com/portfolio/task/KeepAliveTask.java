package com.portfolio.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class KeepAliveTask {

    private static final Logger logger = LoggerFactory.getLogger(KeepAliveTask.class);
    private final RestTemplate restTemplate = new RestTemplate();
    
    // The public URL of the Render deployment
    private final String APP_URL = "https://portfolio-iw85.onrender.com/";

    // Runs every 14 minutes (840000 ms) to keep the Render free tier instance awake
    @Scheduled(fixedRate = 840000)
    public void pingSelf() {
        try {
            logger.info("Keep-alive pinging: {}", APP_URL);
            String response = restTemplate.getForObject(APP_URL, String.class);
            if (response != null) {
                logger.info("Keep-alive ping successful.");
            }
        } catch (Exception e) {
            logger.error("Keep-alive ping failed: {}", e.getMessage());
        }
    }
}
