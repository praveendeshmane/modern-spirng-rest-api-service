package com.example.modernrest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProblemDetailsResponse(
        String type,
        String title,
        int status,
        String detail,
        String instance,
        OffsetDateTime timestamp,
        Map<String, String> errors
) {}
