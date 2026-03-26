package com.example.tms.repository;

import com.example.tms.entity.Enrollment;
import com.example.tms.entity.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    @Query("""
           select e from Enrollment e
           join fetch e.student s
           where e.tutorClass.id = :classId and e.status = :status
           order by s.name asc
           """)
    List<Enrollment> findByTutorClassIdAndStatus(UUID classId, EnrollmentStatus status);
}
