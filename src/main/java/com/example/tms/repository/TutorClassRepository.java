package com.example.tms.repository;

import com.example.tms.entity.TutorClass;
import com.example.tms.entity.enums.ClassStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TutorClassRepository extends JpaRepository<TutorClass, UUID> {
    @Query("""
           select tc from TutorClass tc
           join fetch tc.subject s
           where tc.tutor.id = :tutorId
           """)
    List<TutorClass> findByTutorId(UUID tutorId);

    @Query("""
           select tc from TutorClass tc
           join fetch tc.subject s
           where tc.status = :status
           order by tc.createdAt desc
           """)
    List<TutorClass> findByStatusOrderByCreatedAtDesc(ClassStatus status);

    @Query("""
           select tc from TutorClass tc
           join fetch tc.subject s
           where tc.id = :classId
           """)
    Optional<TutorClass> findDetailedById(UUID classId);
}
