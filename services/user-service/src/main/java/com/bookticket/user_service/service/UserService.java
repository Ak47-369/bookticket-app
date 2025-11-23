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

import java.util.Set;

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
    public UserSummary createUser(CreateUserRequest createUserRequest) {
        String lowerCaseEmail = createUserRequest.email().toLowerCase();
        if (userRepository.existsByUsername(createUserRequest.username())) {
            throw new IllegalStateException("Username already exists");
        }
        if (userRepository.existsByEmail(lowerCaseEmail)) {
            throw new IllegalStateException("Email already exists");
        }

        User user = new User();
        user.setUsername(createUserRequest.username());
        user.setEmail(lowerCaseEmail);
        user.setPassword(passwordEncoder.encode(createUserRequest.password()));
        user.setRoles(Set.of(UserRole.USER, UserRole.ADMIN));// Temp Changes
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
    public JwtResponse updateUserByEmail(String email, UpdateUserRequest updateUserRequest) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User ", "email", email));

        if (updateUserRequest.username() != null && !user.getUsername().equals(updateUserRequest.username())) {
            if (userRepository.existsByUsername(updateUserRequest.username())) {
                throw new IllegalStateException("Username already exists");
            }
            user.setUsername(updateUserRequest.username());
        }

        if (updateUserRequest.email() != null && !user.getEmail().equals(updateUserRequest.email())) {
            String lowerCaseEmail = updateUserRequest.email().toLowerCase();
            if (userRepository.existsByEmail(lowerCaseEmail)) {
                throw new IllegalStateException("Email already exists");
            }
            user.setEmail(lowerCaseEmail);
        }

        // TODO - Add password update

        User savedUser = userRepository.save(user);
        log.info("User updated Successfully: {}", savedUser.getUsername());

        final CustomUserDetails userDetails = new CustomUserDetails(savedUser);
        final String jwt = jwtUtils.generateToken(userDetails);
        final long expiresIn = jwtUtils.extractExpiration(jwt).getTime();

        return new JwtResponse(jwt, expiresIn);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.principal.username")
    public void deleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        userRepository.delete(user);
        log.info("User deleted Successfully: {}", email);
    }
}
