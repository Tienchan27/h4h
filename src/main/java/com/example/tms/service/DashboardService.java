package com.example.tms.service;

import com.example.tms.api.dto.dashboard.TutorDashboardResponse;
import com.example.tms.api.dto.dashboard.TutorSummaryResponse;
import com.example.tms.entity.TutorPayout;
import com.example.tms.entity.User;
import com.example.tms.entity.enums.RoleName;
import com.example.tms.exception.ApiException;
import com.example.tms.repository.TutorPayoutRepository;
import com.example.tms.repository.UserRepository;
import com.example.tms.security.RoleGuard;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
public class DashboardService {
    private final TutorPayoutRepository tutorPayoutRepository;
    private final UserRepository userRepository;
    private final RoleGuard roleGuard;

    public DashboardService(
            TutorPayoutRepository tutorPayoutRepository,
            UserRepository userRepository,
            RoleGuard roleGuard
    ) {
        this.tutorPayoutRepository = tutorPayoutRepository;
        this.userRepository = userRepository;
        this.roleGuard = roleGuard;
    }

    public List<TutorSummaryResponse> adminTutorSummary(User admin, YearMonth month) {
        roleGuard.requireRole(admin, RoleName.ADMIN);
        return tutorPayoutRepository.findByYearAndMonth(month.getYear(), month.getMonthValue())
                .stream()
                .map(this::toSummary)
                .toList();
    }

    public TutorDashboardResponse adminTutorDetail(User admin, UUID tutorId, YearMonth month) {
        roleGuard.requireRole(admin, RoleName.ADMIN);
        TutorPayout payout = tutorPayoutRepository.findByTutorIdAndYearAndMonth(tutorId, month.getYear(), month.getMonthValue())
                .orElseThrow(() -> new ApiException("Payout not found"));
        return toDashboard(payout);
    }

    public List<TutorDashboardResponse> tutorSelf(User tutor) {
        roleGuard.requireRole(tutor, RoleName.TUTOR);
        return tutorPayoutRepository.findByTutorIdOrderByYearDescMonthDesc(tutor.getId())
                .stream()
                .map(this::toDashboard)
                .toList();
    }

    private TutorSummaryResponse toSummary(TutorPayout payout) {
        return new TutorSummaryResponse(
                payout.getTutor().getId(),
                payout.getTutor().getEmail(),
                payout.getGrossRevenue(),
                payout.getNetSalary(),
                payout.getStatus().name()
        );
    }

    private TutorDashboardResponse toDashboard(TutorPayout payout) {
        return new TutorDashboardResponse(
                payout.getYear(),
                payout.getMonth(),
                payout.getGrossRevenue(),
                payout.getNetSalary(),
                payout.getStatus().name()
        );
    }
}
