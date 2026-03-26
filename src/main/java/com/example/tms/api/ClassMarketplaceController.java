package com.example.tms.api;

import com.example.tms.api.dto.classes.ApplyClassResponse;
import com.example.tms.api.dto.classes.AvailableClassResponse;
import com.example.tms.api.dto.classes.PublishedClassResponse;
import com.example.tms.api.dto.classes.UpdateClassDisplayNameRequest;
import com.example.tms.security.CurrentUserResolver;
import com.example.tms.service.ClassAssignmentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/classes")
public class ClassMarketplaceController {
    private final ClassAssignmentService classAssignmentService;
    private final CurrentUserResolver currentUserResolver;

    public ClassMarketplaceController(ClassAssignmentService classAssignmentService, CurrentUserResolver currentUserResolver) {
        this.classAssignmentService = classAssignmentService;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping("/available")
    public List<AvailableClassResponse> available() {
        return classAssignmentService.listAvailableClasses(currentUserResolver.requireUser());
    }

    @PostMapping("/{classId}/apply")
    public ApplyClassResponse apply(@PathVariable UUID classId) {
        return classAssignmentService.applyClass(currentUserResolver.requireUser(), classId);
    }

    @PatchMapping("/{classId}/display-name")
    public PublishedClassResponse updateDisplayName(
            @PathVariable UUID classId,
            @Valid @RequestBody UpdateClassDisplayNameRequest request
    ) {
        return classAssignmentService.updateClassDisplayName(currentUserResolver.requireUser(), classId, request.displayName());
    }
}
