package com.hirenest.repository;

import com.hirenest.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
    long countByRecruiterId(Long recruiterId);

    @Query("SELECT j FROM Job j JOIN FETCH j.recruiter r JOIN FETCH r.user WHERE j.id = :id")
    Optional<Job> findByIdWithRecruiter(@Param("id") Long id);
}
