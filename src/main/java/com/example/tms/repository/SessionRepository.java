package com.example.tms.repository;

import com.example.tms.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, UUID> {
    List<Session> findByPayrollMonth(String payrollMonth);
    List<Session> findByTutorClassTutorIdAndPayrollMonth(UUID tutorId, String payrollMonth);
    Slice<Session> findByTutorClassTutorIdAndPayrollMonth(UUID tutorId, String payrollMonth, Pageable pageable);
    long countByTutorClassId(UUID tutorClassId);
    Optional<Session> findTopByTutorClassIdOrderByDateDesc(UUID tutorClassId);
}
