package com.hirenest.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "saved_jobs", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"applicant_id", "job_id"})
})
public class SavedJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private Applicant applicant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Column(nullable = false, updatable = false)
    private Instant savedAt = Instant.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Applicant getApplicant() { return applicant; }
    public void setApplicant(Applicant applicant) { this.applicant = applicant; }
    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }
    public Instant getSavedAt() { return savedAt; }
    public void setSavedAt(Instant savedAt) { this.savedAt = savedAt; }
}
