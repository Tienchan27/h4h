package com.example.tms.security;

import com.example.tms.entity.User;
import com.example.tms.exception.ApiException;
import com.example.tms.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CurrentUserResolver {
    private final UserRepository userRepository;

    public CurrentUserResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User requireUser(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        if (userId == null || userId.isBlank()) {
            throw new ApiException("Missing X-User-Id header");
        }
        return userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ApiException("User not found"));
    }
}
