package com.example.modernrest.service;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class ApiMetricsService {

    private final MeterRegistry meterRegistry;

    public ApiMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void increment(String endpoint, String method) {
        meterRegistry.counter("modernrest_api_requests_total", "endpoint", endpoint, "method", method).increment();
    }
}
