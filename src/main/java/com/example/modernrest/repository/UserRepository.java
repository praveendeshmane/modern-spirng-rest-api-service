package com.example.modernrest.repository;
import com.example.modernrest.entity.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.UUID;
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    @Query("SELECT u FROM User u WHERE (:username IS NULL OR u.username LIKE %:username%) AND (:email IS NULL OR u.email LIKE %:email%) AND (:status IS NULL OR u.status = :status)")
    Page<User> findUsersWithFilters(@Param("username") String username, @Param("email") String email, @Param("status") UserStatus status, Pageable pageable);
}
