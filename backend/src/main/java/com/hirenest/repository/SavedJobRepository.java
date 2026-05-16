package com.hirenest.repository;

import com.hirenest.entity.SavedJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {
    List<SavedJob> findByApplicantIdOrderBySavedAtDesc(Long applicantId);

    @Query("SELECT s FROM SavedJob s JOIN FETCH s.job j JOIN FETCH j.recruiter r JOIN FETCH r.user " +
           "WHERE s.applicant.id = :applicantId ORDER BY s.savedAt DESC")
    List<SavedJob> findByApplicantIdWithJobDetails(@Param("applicantId") Long applicantId);
    Optional<SavedJob> findByApplicantIdAndJobId(Long applicantId, Long jobId);
    boolean existsByApplicantIdAndJobId(Long applicantId, Long jobId);
}
