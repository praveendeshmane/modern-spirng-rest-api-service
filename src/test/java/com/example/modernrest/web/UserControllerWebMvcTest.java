package com.example.modernrest.web;

import com.example.modernrest.auth.JwtAuthenticationFilter;
import com.example.modernrest.controller.UserController;
import com.example.modernrest.controller.UserModelAssembler;
import com.example.modernrest.dto.PagedResponse;
import com.example.modernrest.dto.UserResponse;
import com.example.modernrest.entity.UserStatus;
import com.example.modernrest.service.ApiMetricsService;
import com.example.modernrest.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserModelAssembler userModelAssembler;

    @MockBean
    private ApiMetricsService apiMetricsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    void shouldReturnPagedUsers() throws Exception {
        UserResponse user = new UserResponse(UUID.randomUUID(), "mvcuser", "mvc@example.com", "Mvc", "User", UserStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now());
        Page<UserResponse> page = new PageImpl<>(List.of(user), PageRequest.of(0, 20), 1);

        when(userService.getAllUsers(any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("mvcuser"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}
