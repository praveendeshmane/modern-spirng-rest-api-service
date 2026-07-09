package com.example.modernrest.unit;

import com.example.modernrest.dto.CreateUserRequest;
import com.example.modernrest.dto.UpdateUserRequest;
import com.example.modernrest.entity.User;
import com.example.modernrest.entity.UserStatus;
import com.example.modernrest.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toEntity_shouldMapCreateRequest() {
        CreateUserRequest request = new CreateUserRequest("alice", "alice@example.com", "Alice", "Wonder");

        User user = mapper.toEntity(request);

        assertEquals("alice", user.getUsername());
        assertEquals("alice@example.com", user.getEmail());
        assertEquals("Alice", user.getFirstName());
        assertEquals("Wonder", user.getLastName());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
    }

    @Test
    void updateEntityFromDto_shouldIgnoreNulls() {
        User user = new User();
        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setFirstName("Alice");
        user.setLastName("Wonder");
        user.setStatus(UserStatus.ACTIVE);

        UpdateUserRequest update = new UpdateUserRequest(null, "Alicia", null, UserStatus.INACTIVE);
        mapper.updateEntityFromDto(update, user);

        assertEquals("alice@example.com", user.getEmail());
        assertEquals("Alicia", user.getFirstName());
        assertEquals("Wonder", user.getLastName());
        assertEquals(UserStatus.INACTIVE, user.getStatus());
    }
}
