package com.example.tms.api;

import com.example.tms.api.dto.session.CreateSessionRequest;
import com.example.tms.api.dto.session.UpdateSessionFinancialRequest;
import com.example.tms.entity.Session;
import com.example.tms.security.CurrentUserResolver;
import com.example.tms.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sessions")
public class SessionController {
    private final SessionService sessionService;
    private final CurrentUserResolver currentUserResolver;

    public SessionController(SessionService sessionService, CurrentUserResolver currentUserResolver) {
        this.sessionService = sessionService;
        this.currentUserResolver = currentUserResolver;
    }

    @PostMapping
    public Session create(@Valid @RequestBody CreateSessionRequest request, HttpServletRequest httpRequest) {
        return sessionService.create(currentUserResolver.requireUser(httpRequest), request);
    }

    @PatchMapping("/{sessionId}/financial")
    public Session updateFinancial(
            @PathVariable UUID sessionId,
            @Valid @RequestBody UpdateSessionFinancialRequest request,
            HttpServletRequest httpRequest
    ) {
        return sessionService.updateFinancial(currentUserResolver.requireUser(httpRequest), sessionId, request);
    }

    @GetMapping
    public List<Session> byMonth(@RequestParam String payrollMonth) {
        return sessionService.getByPayrollMonth(payrollMonth);
    }
}
