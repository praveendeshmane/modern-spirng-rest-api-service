package com.example.modernrest.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
public record CreateUserRequest(
    @NotBlank @Size(min=3,max=50) String username,
    @NotBlank @Email String email,
    @NotBlank @Size(max=100) @JsonProperty("first_name") String firstName,
    @NotBlank @Size(max=100) @JsonProperty("last_name") String lastName
) {}
