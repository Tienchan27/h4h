package com.example.tms.repository;

import com.example.tms.entity.TutorClass;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TutorClassRepository extends JpaRepository<TutorClass, UUID> {
    @Query("""
           select tc from TutorClass tc
           join fetch tc.subject s
           where tc.tutor.id = :tutorId
           """)
    List<TutorClass> findByTutorId(UUID tutorId);
}
