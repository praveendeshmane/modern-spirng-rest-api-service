package com.example.modernrest.controller;

import com.example.modernrest.service.AsyncReportService;
import com.example.modernrest.service.MessagePublisherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/ops")
public class OperationsController {

    private final AsyncReportService asyncReportService;
    private final MessagePublisherService messagePublisherService;

    public OperationsController(AsyncReportService asyncReportService, MessagePublisherService messagePublisherService) {
        this.asyncReportService = asyncReportService;
        this.messagePublisherService = messagePublisherService;
    }

    @PostMapping("/reports/users")
    public CompletableFuture<ResponseEntity<Map<String, String>>> generateReport() {
        return asyncReportService.generateUserSummaryReport()
                .thenApply(msg -> ResponseEntity.accepted().body(Map.of("message", msg)));
    }

    @PostMapping("/messages")
    public ResponseEntity<Map<String, String>> publish(@RequestBody Map<String, String> request) {
        String message = request.getOrDefault("message", "default message");
        messagePublisherService.publish(message);
        return ResponseEntity.accepted().body(Map.of("status", "queued"));
    }
}
