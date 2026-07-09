package com.example.modernrest.dto;
import com.example.modernrest.entity.UserStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
public record UpdateUserRequest(
    @Email String email,
    @Size(max=100) @JsonProperty("first_name") String firstName,
    @Size(max=100) @JsonProperty("last_name") String lastName,
    UserStatus status
) {}
