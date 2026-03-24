package com.example.tms.security;

import com.example.tms.entity.UserRole;
import com.example.tms.entity.enums.UserStatus;
import com.example.tms.entity.enums.UserRoleStatus;
import com.example.tms.repository.UserRepository;
import com.example.tms.repository.UserRoleRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRoleRepository userRoleRepository,
            UserRepository userRepository
    ) {
        this.jwtService = jwtService;
        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);
            jwtService.validateAccessToken(token);

            UUID userId = jwtService.extractUserId(token);
            boolean isActiveUser = userRepository.findById(userId)
                    .map(user -> user.getStatus() == UserStatus.ACTIVE)
                    .orElse(false);
            if (!isActiveUser) {
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }
            List<GrantedAuthority> authorities = userRoleRepository
                    .findByUserIdAndStatus(userId, UserRoleStatus.ACTIVE)
                    .stream()
                    .map(UserRole::getRole)
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
                    .map(GrantedAuthority.class::cast)
                    .toList();

            // Create authentication object
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    authorities
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Set authentication in SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (RuntimeException ex) {
            // Token validation failed - let request continue without authentication
            // SecurityConfig will reject if endpoint requires authentication
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
