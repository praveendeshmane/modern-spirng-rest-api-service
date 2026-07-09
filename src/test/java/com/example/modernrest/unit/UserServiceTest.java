package com.example.modernrest.unit;

import com.example.modernrest.dto.CreateUserRequest;
import com.example.modernrest.dto.UpdateUserRequest;
import com.example.modernrest.dto.UserResponse;
import com.example.modernrest.entity.User;
import com.example.modernrest.entity.UserStatus;
import com.example.modernrest.exception.DuplicateResourceException;
import com.example.modernrest.exception.ResourceNotFoundException;
import com.example.modernrest.mapper.UserMapper;
import com.example.modernrest.repository.UserRepository;
import com.example.modernrest.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, userMapper);
    }

    @Test
    void createUser_shouldThrowWhenUsernameAlreadyExists() {
        CreateUserRequest request = new CreateUserRequest("testuser", "test@example.com", "Test", "User");
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_shouldCreateSuccessfully() {
        CreateUserRequest request = new CreateUserRequest("testuser", "test@example.com", "Test", "User");
        User entity = new User();
        entity.setUsername("testuser");
        entity.setEmail("test@example.com");
        entity.setFirstName("Test");
        entity.setLastName("User");
        entity.setStatus(UserStatus.ACTIVE);

        UserResponse response = new UserResponse(null, "testuser", "test@example.com", "Test", "User", UserStatus.ACTIVE, null, null);

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(entity);
        when(userMapper.toResponse(entity)).thenReturn(response);

        UserResponse created = userService.createUser(request);

        assertEquals("testuser", created.username());
        assertEquals("test@example.com", created.email());
        verify(userRepository).save(entity);
    }

    @Test
    void updateUser_shouldThrowWhenUserNotFound() {
        UUID id = UUID.randomUUID();
        UpdateUserRequest request = new UpdateUserRequest("new@example.com", "New", "Name", UserStatus.INACTIVE);
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(id, request));
    }
}
