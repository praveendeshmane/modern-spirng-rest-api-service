package com.example.modernrest.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
@Entity @Table(name="users") @EntityListeners(AuditingEntityListener.class)
public class User {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable=false, unique=true, length=50) private String username;
    @Column(nullable=false, unique=true) private String email;
    @Column(nullable=false, length=100) private String firstName;
    @Column(nullable=false, length=100) private String lastName;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private UserStatus status = UserStatus.ACTIVE;
    @OneToMany(mappedBy="user", cascade=CascadeType.ALL, fetch=FetchType.LAZY) private List<Order> orders = new ArrayList<>();
    @CreatedDate @Column(nullable=false, updatable=false) private LocalDateTime createdAt;
    @LastModifiedDate @Column(nullable=false) private LocalDateTime updatedAt;
    @Version private Long version;
    public UUID getId(){return id;} public String getUsername(){return username;} public void setUsername(String username){this.username=username;}
    public String getEmail(){return email;} public void setEmail(String email){this.email=email;}
    public String getFirstName(){return firstName;} public void setFirstName(String firstName){this.firstName=firstName;}
    public String getLastName(){return lastName;} public void setLastName(String lastName){this.lastName=lastName;}
    public UserStatus getStatus(){return status;} public void setStatus(UserStatus status){this.status=status;}
    public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;}
}
