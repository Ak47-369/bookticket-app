package com.bookticket.user_service.dto;

import com.bookticket.user_service.entity.User;
import com.bookticket.user_service.enums.UserRole;
import lombok.Builder;
import java.util.List;

@Builder
public record UserSummary(
        String id,
        String username,
        String email,
        List<String> roles
) {
    public static UserSummary fromUser(User user) {
        return UserSummary.builder()
                .id(user.getId().toString())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles().stream().map(UserRole::name).toList())
                .build();
    }
}
