package com.hirenest.repository;

import com.hirenest.entity.Application;
import com.hirenest.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByApplicantIdOrderByAppliedAtDesc(Long applicantId);
    List<Application> findByJobRecruiterIdOrderByAppliedAtDesc(Long recruiterId);
    List<Application> findByJobIdOrderByAppliedAtDesc(Long jobId);
    Optional<Application> findByJobIdAndApplicantId(Long jobId, Long applicantId);
    boolean existsByJobIdAndApplicantId(Long jobId, Long applicantId);
    long countByJobRecruiterId(Long recruiterId);
}
