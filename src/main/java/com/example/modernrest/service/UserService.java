package com.example.modernrest.service;
import com.example.modernrest.dto.*;
import com.example.modernrest.entity.*;
import com.example.modernrest.exception.*;
import com.example.modernrest.events.UserChangedEvent;
import com.example.modernrest.mapper.UserMapper;
import com.example.modernrest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Service @Transactional
public class UserService {
    private final UserRepository userRepository; private final UserMapper userMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public UserService(UserRepository userRepository, UserMapper userMapper){this.userRepository=userRepository; this.userMapper=userMapper;}
    public UserResponse createUser(CreateUserRequest request){
        if(userRepository.existsByUsername(request.username())) throw new DuplicateResourceException("Username already exists: "+request.username());
        if(userRepository.existsByEmail(request.email())) throw new DuplicateResourceException("Email already exists: "+request.email());
        User saved = userRepository.save(userMapper.toEntity(request));
        publishUserEvent(saved.getId(), saved.getUsername(), UserChangedEvent.Action.CREATED);
        return userMapper.toResponse(saved);
    }
    @Cacheable(value="users", key="#id") @Transactional(readOnly=true)
    public UserResponse getUserById(UUID id){
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with ID: "+id));
        return userMapper.toResponse(user);
    }
    @Transactional(readOnly=true)
    public Page<UserResponse> getAllUsers(String username,String email,UserStatus status,Pageable pageable){
        return userRepository.findUsersWithFilters(username,email,status,pageable).map(userMapper::toResponse);
    }
    @CacheEvict(value="users", key="#id")
    public UserResponse updateUser(UUID id, UpdateUserRequest request){
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with ID: "+id));
        if(request.email()!=null && !request.email().equals(user.getEmail()) && userRepository.existsByEmail(request.email())) throw new DuplicateResourceException("Email already exists: "+request.email());
        userMapper.updateEntityFromDto(request,user);
        User saved = userRepository.save(user);
        publishUserEvent(saved.getId(), saved.getUsername(), UserChangedEvent.Action.UPDATED);
        return userMapper.toResponse(saved);
    }
    @CacheEvict(value="users", key="#id")
    public void deleteUser(UUID id){
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with ID: "+id));
        userRepository.deleteById(id);
        publishUserEvent(user.getId(), user.getUsername(), UserChangedEvent.Action.DELETED);
    }

    private void publishUserEvent(UUID userId, String username, UserChangedEvent.Action action) {
        if (eventPublisher != null) {
            eventPublisher.publishEvent(new UserChangedEvent(userId, username, action));
        }
    }
}
