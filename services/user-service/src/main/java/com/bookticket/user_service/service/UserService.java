package com.bookticket.user_service.service;

import com.bookticket.user_service.dto.*;
import com.bookticket.user_service.entity.User;
import com.bookticket.user_service.enums.UserRole;
import com.bookticket.user_service.exception.ResourceNotFoundException;
import com.bookticket.user_service.repository.UserRepository;
import com.bookticket.user_service.utils.JwtUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public UserSummary createUser(RegisterRequest registerRequest) {
        String lowerCaseEmail = registerRequest.email().toLowerCase();
        if (Boolean.TRUE.equals(userRepository.existsByUsername(registerRequest.username()))) {
            throw new IllegalStateException("Username already exists");
        }
        if (Boolean.TRUE.equals(userRepository.existsByEmail(lowerCaseEmail))) {
            throw new IllegalStateException("Email already exists");
        }

        User user = new User();
        user.setUsername(registerRequest.username());
        user.setEmail(lowerCaseEmail);
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        user.setRoles(Set.of(UserRole.USER)); // Default Role
        User savedUser = userRepository.save(user);
        log.info("User created Successfully: {}", user.getUsername());
        return UserSummary.fromUser(savedUser);
    }

    @Transactional
    public UserSummary createUserWithRoles(CreateUserRequest createUserRequest) {
        String lowerCaseEmail = createUserRequest.email().toLowerCase();
        if (Boolean.TRUE.equals(userRepository.existsByUsername(createUserRequest.username()))) {
            throw new IllegalStateException("Username already exists");
        }
        if (Boolean.TRUE.equals(userRepository.existsByEmail(lowerCaseEmail))) {
            throw new IllegalStateException("Email already exists");
        }

        User user = new User();
        user.setUsername(createUserRequest.username());
        user.setEmail(lowerCaseEmail);
        user.setPassword(passwordEncoder.encode(createUserRequest.password()));
        user.setRoles(new HashSet<>(createUserRequest.roles()));
        User savedUser = userRepository.save(user);
        log.info("User created Successfully: {}", user.getUsername());
        return UserSummary.fromUser(savedUser);
    }

    public UserSummary getUserByEmail(String email){
        log.info("Getting user by Email: {}", email);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return UserSummary.fromUser(user);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    @Transactional
    public LoginResponse updateUserByEmail(String email, UpdateUserRequest updateUserRequest) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User ", "email", email));

        if (updateUserRequest.username() != null && !user.getUsername().equals(updateUserRequest.username())) {
            if (Boolean.TRUE.equals(userRepository.existsByUsername(updateUserRequest.username()))) {
                throw new IllegalStateException("Username already exists");
            }
            user.setUsername(updateUserRequest.username());
        }

        if (updateUserRequest.email() != null && !user.getEmail().equals(updateUserRequest.email())) {
            String lowerCaseEmail = updateUserRequest.email().toLowerCase();
            if (Boolean.TRUE.equals(userRepository.existsByEmail(lowerCaseEmail))) {
                throw new IllegalStateException("Email already exists");
            }
            user.setEmail(lowerCaseEmail);
        }

        User savedUser = userRepository.save(user);
        UserSummary userSummary = UserSummary.fromUser(savedUser);
        log.info("User updated Successfully: {}", savedUser.getUsername());

        final CustomUserDetails userDetails = new CustomUserDetails(savedUser);
        final String jwt = jwtUtils.generateToken(userDetails);
        final long expiresIn = jwtUtils.extractExpiration(jwt).getTime();

        return new LoginResponse(userSummary, new JwtResponse(jwt, expiresIn));
    }

    @Transactional
    public void updatePasswordByEmail(String email, UpdatePasswordRequest updatePasswordRequest) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        if (!passwordEncoder.matches(updatePasswordRequest.currentPassword(), user.getPassword())) {
            throw new IllegalStateException("Current password is incorrect");
        }
        if (!updatePasswordRequest.newPassword().equals(updatePasswordRequest.confirmNewPassword())) {
            throw new IllegalStateException("New password and confirm password do not match");
        }
        user.setPassword(passwordEncoder.encode(updatePasswordRequest.newPassword()));
        userRepository.save(user);
        log.info("Password updated Successfully for user: {}", email);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.principal.username")
    public void deleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        userRepository.delete(user);
        log.info("User deleted Successfully: {}", email);
    }
}
