package com.hirenest.repository;

import com.hirenest.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
    long countByRecruiterId(Long recruiterId);

    @EntityGraph(attributePaths = {"recruiter", "recruiter.user", "skills"})
    Page<Job> findAll(Specification<Job> spec, Pageable pageable);

    @Query("SELECT j FROM Job j JOIN FETCH j.recruiter r JOIN FETCH r.user WHERE j.id = :id")
    Optional<Job> findByIdWithRecruiter(@Param("id") Long id);

    @Query("""
            SELECT j FROM Job j
            JOIN FETCH j.recruiter r
            JOIN FETCH r.user
            WHERE r.id = :recruiterId
            ORDER BY j.postedAt DESC
            """)
    List<Job> findByRecruiterIdWithDetails(@Param("recruiterId") Long recruiterId);

    @Query("""
            SELECT DISTINCT j FROM Job j
            JOIN FETCH j.recruiter r
            JOIN FETCH r.user
            WHERE j.active = true
            ORDER BY j.postedAt DESC
            """)
    List<Job> findAllActiveWithRecruiter();

    @Query(
            value = """
                    SELECT DISTINCT j FROM Job j
                    JOIN FETCH j.recruiter r
                    JOIN FETCH r.user
                    WHERE j.active = true
                    """,
            countQuery = "SELECT COUNT(j) FROM Job j WHERE j.active = true"
    )
    Page<Job> findBrowsePage(Pageable pageable);

    List<Job> findByActiveFalse();
}
