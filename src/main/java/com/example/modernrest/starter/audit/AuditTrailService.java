package com.example.modernrest.starter.audit;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class AuditTrailService {

    private final AuditTrailProperties properties;
    private final Deque<String> entries = new ArrayDeque<>();

    public AuditTrailService(AuditTrailProperties properties) {
        this.properties = properties;
    }

    public synchronized void record(String message) {
        if (!properties.isEnabled()) {
            return;
        }
        entries.addFirst(Instant.now() + " | " + message);
        while (entries.size() > properties.getMaxEntries()) {
            entries.removeLast();
        }
    }

    public synchronized List<String> latest() {
        return new ArrayList<>(entries);
    }
}
