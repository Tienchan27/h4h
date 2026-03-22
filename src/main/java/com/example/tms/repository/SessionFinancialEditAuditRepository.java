package com.example.tms.repository;

import com.example.tms.entity.SessionFinancialEditAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SessionFinancialEditAuditRepository extends JpaRepository<SessionFinancialEditAudit, UUID> {
}
