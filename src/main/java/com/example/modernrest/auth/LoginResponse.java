package com.example.modernrest.auth;

public record LoginResponse(String accessToken, String tokenType, long expiresInMs) {}
