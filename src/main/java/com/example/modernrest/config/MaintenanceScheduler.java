package com.example.modernrest.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class MaintenanceScheduler {

    private static final Logger log = LoggerFactory.getLogger(MaintenanceScheduler.class);

    @Scheduled(fixedDelayString = "${app.scheduler.cleanup-delay-ms:300000}")
    public void runHealthCleanupTick() {
        log.debug("Scheduled cleanup tick at {}", Instant.now());
    }
}
