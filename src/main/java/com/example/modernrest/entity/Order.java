package com.example.modernrest.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
@Entity @Table(name="orders") @EntityListeners(AuditingEntityListener.class)
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable=false, unique=true) private String orderNumber;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id", nullable=false) private User user;
    @Column(nullable=false, precision=10, scale=2) private BigDecimal totalAmount;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private OrderStatus status = OrderStatus.PENDING;
    @CreatedDate @Column(nullable=false, updatable=false) private LocalDateTime createdAt;
    @LastModifiedDate @Column(nullable=false) private LocalDateTime updatedAt;
}
