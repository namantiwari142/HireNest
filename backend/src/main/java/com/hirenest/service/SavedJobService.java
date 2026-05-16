package com.hirenest.service;

import com.hirenest.dto.JobResponse;
import com.hirenest.entity.Applicant;
import com.hirenest.entity.SavedJob;
import com.hirenest.entity.User;
import com.hirenest.exception.BadRequestException;
import com.hirenest.exception.ResourceNotFoundException;
import com.hirenest.repository.ApplicantRepository;
import com.hirenest.repository.ApplicationRepository;
import com.hirenest.repository.JobRepository;
import com.hirenest.repository.SavedJobRepository;
import com.hirenest.util.JobMapper;
import com.hirenest.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SavedJobService {

    private final SavedJobRepository savedJobRepository;
    private final ApplicantRepository applicantRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final SecurityUtils securityUtils;

    public SavedJobService(
            SavedJobRepository savedJobRepository,
            ApplicantRepository applicantRepository,
            JobRepository jobRepository,
            ApplicationRepository applicationRepository,
            SecurityUtils securityUtils
    ) {
        this.savedJobRepository = savedJobRepository;
        this.applicantRepository = applicantRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public void toggleSave(Long jobId) {
        Applicant applicant = getApplicant();
        if (savedJobRepository.existsByApplicantIdAndJobId(applicant.getId(), jobId)) {
            savedJobRepository.findByApplicantIdAndJobId(applicant.getId(), jobId)
                    .ifPresent(savedJobRepository::delete);
        } else {
            SavedJob saved = new SavedJob();
            saved.setApplicant(applicant);
            saved.setJob(jobRepository.findById(jobId)
                    .orElseThrow(() -> new ResourceNotFoundException("Job not found")));
            savedJobRepository.save(saved);
        }
    }

    @Transactional(readOnly = true)
    public List<JobResponse> getSavedJobs() {
        Applicant applicant = getApplicant();
        return savedJobRepository.findByApplicantIdWithJobDetails(applicant.getId())
                .stream()
                .map(s -> {
                    JobResponse r = JobMapper.toResponse(s.getJob());
                    r.setSaved(true);
                    r.setApplied(applicationRepository.existsByJobIdAndApplicantId(
                            s.getJob().getId(), applicant.getId()));
                    return r;
                })
                .toList();
    }

    private Applicant getApplicant() {
        User user = securityUtils.getCurrentUser();
        return applicantRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException("Applicant profile not found"));
    }
}
