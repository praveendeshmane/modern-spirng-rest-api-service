package com.example.modernrest.dto;
import com.example.modernrest.entity.UserStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;
public record UserResponse(UUID id, String username, String email,
                           @JsonProperty("first_name") String firstName,
                           @JsonProperty("last_name") String lastName,
                           UserStatus status,
                           @JsonProperty("created_at") LocalDateTime createdAt,
                           @JsonProperty("updated_at") LocalDateTime updatedAt) {}
