package com.example.tms.api;

import com.example.tms.entity.Notification;
import com.example.tms.security.CurrentUserResolver;
import com.example.tms.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final CurrentUserResolver currentUserResolver;

    public NotificationController(NotificationService notificationService, CurrentUserResolver currentUserResolver) {
        this.notificationService = notificationService;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping("/me")
    public List<Notification> myNotifications(HttpServletRequest request) {
        return notificationService.getMyNotifications(currentUserResolver.requireUser(request).getId());
    }

    @PostMapping("/{id}/read")
    public Notification markRead(@PathVariable UUID id) {
        return notificationService.markRead(id);
    }
}
