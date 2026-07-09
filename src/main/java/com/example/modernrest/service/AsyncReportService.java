package com.example.modernrest.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncReportService {

    private static final Logger log = LoggerFactory.getLogger(AsyncReportService.class);

    @Async
    public CompletableFuture<String> generateUserSummaryReport() {
        log.info("Async report generation started at {}", Instant.now());
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return CompletableFuture.failedFuture(e);
        }
        String result = "User summary report generated at " + Instant.now();
        log.info(result);
        return CompletableFuture.completedFuture(result);
    }
}
