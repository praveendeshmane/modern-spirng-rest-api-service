package com.example.modernrest.dto;

import java.util.List;
import java.util.Map;

public record PagedResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        Map<String, String> links
) {}
