package com.example.modernrest.repository;

import com.example.modernrest.entity.User;
import com.example.modernrest.entity.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryDataJpaTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM orders");
        jdbcTemplate.update("DELETE FROM users");

        insertUser(UUID.randomUUID(), "repo_user_1", "repo1@example.com", "Repo", "One", "ACTIVE");
        insertUser(UUID.randomUUID(), "repo_user_2", "repo2@example.com", "Repo", "Two", "INACTIVE");
    }

    @Test
    void existsByUsername_shouldReturnTrueWhenPresent() {
        assertTrue(userRepository.existsByUsername("repo_user_1"));
        assertFalse(userRepository.existsByUsername("missing_user"));
    }

    @Test
    void findUsersWithFilters_shouldFilterByStatus() {
        Page<User> activeUsers = userRepository.findUsersWithFilters(null, null, UserStatus.ACTIVE, PageRequest.of(0, 10));
        assertEquals(1, activeUsers.getTotalElements());
        assertEquals("repo_user_1", activeUsers.getContent().getFirst().getUsername());
    }

    private void insertUser(UUID id, String username, String email, String firstName, String lastName, String status) {
        LocalDateTime now = LocalDateTime.now();
        jdbcTemplate.update(
                "INSERT INTO users (id, username, email, first_name, last_name, status, created_at, updated_at, version) VALUES (?,?,?,?,?,?,?,?,?)",
                id, username, email, firstName, lastName, status, now, now, 0L
        );
    }
}
