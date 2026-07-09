package com.example.modernrest.controller;

import com.example.modernrest.starter.audit.AuditTrailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/platform")
public class PlatformController {

    private final AuditTrailService auditTrailService;

    public PlatformController(AuditTrailService auditTrailService) {
        this.auditTrailService = auditTrailService;
    }

    @GetMapping("/audit-trail")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, List<String>>> auditTrail() {
        return ResponseEntity.ok(Map.of("events", auditTrailService.latest()));
    }
}
