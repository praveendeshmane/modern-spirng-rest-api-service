package com.example.modernrest.mapper;
import com.example.modernrest.dto.*;
import com.example.modernrest.entity.User;
import org.mapstruct.*;
@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(CreateUserRequest request);
    UserResponse toResponse(User user);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateUserRequest request, @MappingTarget User user);
}
