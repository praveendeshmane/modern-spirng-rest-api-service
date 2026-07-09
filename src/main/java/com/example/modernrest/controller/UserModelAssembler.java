package com.example.modernrest.controller;

import com.example.modernrest.dto.UserResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler {

    public EntityModel<UserResponse> toModel(UserResponse user) {
        return EntityModel.of(user,
                linkTo(methodOn(UserController.class).getUserById(user.id())).withSelfRel(),
                linkTo(methodOn(UserController.class).getUserResource(user.id())).withRel("resource"),
                linkTo(methodOn(UserController.class).getAllUsers(null, null, null, org.springframework.data.domain.Pageable.ofSize(20))).withRel("users")
        );
    }
}
