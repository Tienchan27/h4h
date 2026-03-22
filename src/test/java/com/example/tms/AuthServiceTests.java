package com.example.tms;

import com.example.tms.api.dto.auth.RegisterRequest;
import com.example.tms.entity.User;
import com.example.tms.entity.enums.UserStatus;
import com.example.tms.repository.UserRepository;
import com.example.tms.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceTests {
    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;

    @Test
    void registerCreatesPendingUser() {
        authService.register(new RegisterRequest("Student One", "student1@example.com", "password123"));
        User user = userRepository.findByEmail("student1@example.com").orElseThrow();
        assertEquals(UserStatus.PENDING_VERIFICATION, user.getStatus());
        assertTrue(user.getPassword() != null && !user.getPassword().isBlank());
    }
}
