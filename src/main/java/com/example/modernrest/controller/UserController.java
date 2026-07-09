package com.example.modernrest.controller;

import com.example.modernrest.dto.CreateUserRequest;
import com.example.modernrest.dto.PagedResponse;
import com.example.modernrest.dto.UpdateUserRequest;
import com.example.modernrest.dto.UserResponse;
import com.example.modernrest.entity.UserStatus;
import com.example.modernrest.service.ApiMetricsService;
import com.example.modernrest.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/users", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class UserController {
    private final UserService userService;
    private final UserModelAssembler userModelAssembler;
    private final ApiMetricsService apiMetricsService;

    public UserController(UserService userService, UserModelAssembler userModelAssembler, ApiMetricsService apiMetricsService) {
        this.userService = userService;
        this.userModelAssembler = userModelAssembler;
        this.apiMetricsService = apiMetricsService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        apiMetricsService.increment("/api/v1/users", "POST");
        return new ResponseEntity<>(userService.createUser(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        apiMetricsService.increment("/api/v1/users/{id}", "GET");
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/{id}/resource")
    public ResponseEntity<EntityModel<UserResponse>> getUserResource(@PathVariable UUID id) {
        apiMetricsService.increment("/api/v1/users/{id}/resource", "GET");
        return ResponseEntity.ok(userModelAssembler.toModel(userService.getUserById(id)));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<UserResponse>> getAllUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) UserStatus status,
            @PageableDefault(size = 20) Pageable pageable) {

        apiMetricsService.increment("/api/v1/users", "GET");
        Page<UserResponse> page = userService.getAllUsers(username, email, status, pageable);
        Map<String, String> links = buildPagingLinks(pageable.getPageNumber(), pageable.getPageSize(), page.getTotalPages());

        PagedResponse<UserResponse> response = new PagedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                links
        );

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request) {
        apiMetricsService.increment("/api/v1/users/{id}", "PUT");
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        apiMetricsService.increment("/api/v1/users/{id}", "DELETE");
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    private Map<String, String> buildPagingLinks(int page, int size, int totalPages) {
        Map<String, String> links = new LinkedHashMap<>();
        String base = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();

        links.put("self", base + "?page=" + page + "&size=" + size);
        links.put("first", base + "?page=0&size=" + size);
        if (page > 0) {
            links.put("prev", base + "?page=" + (page - 1) + "&size=" + size);
        }
        if (page + 1 < totalPages) {
            links.put("next", base + "?page=" + (page + 1) + "&size=" + size);
        }
        if (totalPages > 0) {
            links.put("last", base + "?page=" + (totalPages - 1) + "&size=" + size);
        }

        return links;
    }
}
