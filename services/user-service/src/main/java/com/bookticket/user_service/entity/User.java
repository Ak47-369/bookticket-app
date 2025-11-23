package com.bookticket.user_service.entity;

import com.bookticket.user_service.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_details")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Column(unique = true,nullable = false)
    @Size(min=3,max = 50)
    private String username;
    @NotBlank(message = "Password is required")
    @Size(min=6, message = "Password must be between 6 and 50 characters")
    private String password;
    @NotBlank(message = "Email is required")
    @Size(min=6,max = 100)
    @Email
    @Column(unique = true, nullable = false)
    private String email;
    private Set<UserRole> roles = new HashSet<>();

    // Auditing Fields
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDate createdAt;
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDate updatedAt;
}
