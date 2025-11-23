package com.bookticket.user_service.service;

import com.bookticket.user_service.entity.User;
import lombok.Data;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@Data
public class CustomUserDetails implements UserDetails {
    private final Long id;
    private final String username;
    private final String userIdAndName;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.username = user.getEmail(); // Using email as the username for authentication
        this.password = user.getPassword();
        this.userIdAndName = user.getId() + "_" + user.getUsername();
        this.authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority( role.name()))
                .toList();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

}
