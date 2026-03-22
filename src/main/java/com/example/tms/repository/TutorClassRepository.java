package com.example.tms.repository;

import com.example.tms.entity.TutorClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TutorClassRepository extends JpaRepository<TutorClass, UUID> {
    List<TutorClass> findByTutorId(UUID tutorId);
}
