package com.example.modernrest.mapper;

import com.example.modernrest.dto.CreateUserRequest;
import com.example.modernrest.dto.UpdateUserRequest;
import com.example.modernrest.dto.UserResponse;
import com.example.modernrest.entity.User;
import com.example.modernrest.entity.UserStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-14T12:51:54+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (JetBrains s.r.o.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toEntity(CreateUserRequest request) {
        if ( request == null ) {
            return null;
        }

        User user = new User();

        user.setUsername( request.username() );
        user.setEmail( request.email() );
        user.setFirstName( request.firstName() );
        user.setLastName( request.lastName() );

        return user;
    }

    @Override
    public UserResponse toResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UUID id = null;
        String username = null;
        String email = null;
        String firstName = null;
        String lastName = null;
        UserStatus status = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        id = user.getId();
        username = user.getUsername();
        email = user.getEmail();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        status = user.getStatus();
        createdAt = user.getCreatedAt();
        updatedAt = user.getUpdatedAt();

        UserResponse userResponse = new UserResponse( id, username, email, firstName, lastName, status, createdAt, updatedAt );

        return userResponse;
    }

    @Override
    public void updateEntityFromDto(UpdateUserRequest request, User user) {
        if ( request == null ) {
            return;
        }

        if ( request.email() != null ) {
            user.setEmail( request.email() );
        }
        if ( request.firstName() != null ) {
            user.setFirstName( request.firstName() );
        }
        if ( request.lastName() != null ) {
            user.setLastName( request.lastName() );
        }
        if ( request.status() != null ) {
            user.setStatus( request.status() );
        }
    }
}
