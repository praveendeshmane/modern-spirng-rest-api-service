package com.example.modernrest.events;

import java.util.UUID;

public record UserChangedEvent(UUID userId, String username, Action action) {
    public enum Action { CREATED, UPDATED, DELETED }
}
