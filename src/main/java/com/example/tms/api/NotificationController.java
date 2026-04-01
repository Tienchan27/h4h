package com.example.tms.api;

import com.example.tms.api.dto.common.SliceResponse;
import com.example.tms.api.dto.notification.NotificationResponse;
import com.example.tms.api.util.PageableGuard;
import com.example.tms.api.dto.notification.NotificationResponse;
import com.example.tms.security.CurrentUserResolver;
import com.example.tms.service.NotificationService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.Set;

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
    public SliceResponse<NotificationResponse> myNotifications(Pageable pageable) {
        Pageable guarded = PageableGuard.guard(
                pageable,
                50,
                Sort.by(Sort.Direction.DESC, "createdAt"),
                Set.of("createdAt")
        );
        Slice<NotificationResponse> slice = notificationService.getMyNotifications(currentUserResolver.requireUserId(), guarded);
        return new SliceResponse<>(
                slice.getContent(),
                slice.hasNext(),
                guarded.getPageNumber(),
                guarded.getPageSize(),
                PageableGuard.sortToString(guarded.getSort())
        );
    }

    @PostMapping("/{id}/read")
    public NotificationResponse markRead(@PathVariable UUID id) {
        return notificationService.markReadResponse(currentUserResolver.requireUserId(), id);
    }
}
