package com.hirenest.service;

import com.hirenest.dto.ApplicationResponse;
import com.hirenest.entity.*;
import com.hirenest.exception.BadRequestException;
import com.hirenest.exception.ResourceNotFoundException;
import com.hirenest.repository.*;
import com.hirenest.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicantRepository applicantRepository;
    private final JobRepository jobRepository;
    private final RecruiterRepository recruiterRepository;
    private final SecurityUtils securityUtils;
    private final NotificationService notificationService;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            ApplicantRepository applicantRepository,
            JobRepository jobRepository,
            RecruiterRepository recruiterRepository,
            SecurityUtils securityUtils,
            NotificationService notificationService
    ) {
        this.applicationRepository = applicationRepository;
        this.applicantRepository = applicantRepository;
        this.jobRepository = jobRepository;
        this.recruiterRepository = recruiterRepository;
        this.securityUtils = securityUtils;
        this.notificationService = notificationService;
    }

    @Transactional
    public ApplicationResponse apply(Long jobId) {
        User user = securityUtils.getCurrentUser();
        Applicant applicant = applicantRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException("Applicant profile not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (applicationRepository.existsByJobIdAndApplicantId(jobId, applicant.getId())) {
            throw new BadRequestException("Already applied to this job");
        }

        Application app = new Application();
        app.setJob(job);
        app.setApplicant(applicant);
        app = applicationRepository.save(app);

        notificationService.create(
                job.getRecruiter().getUser().getId(),
                NotificationType.NEW_APPLICANT,
                "New Application",
                applicant.getUser().getName() + " applied for " + job.getTitle(),
                app.getId()
        );

        return toResponse(app, true);
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getMyApplications() {
        User user = securityUtils.getCurrentUser();
        Applicant applicant = applicantRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Applicant not found"));
        return applicationRepository.findByApplicantIdOrderByAppliedAtDesc(applicant.getId())
                .stream().map(a -> toResponse(a, false)).toList();
    }

    public List<ApplicationResponse> getRecruiterApplications() {
        User user = securityUtils.getCurrentUser();
        Recruiter recruiter = recruiterRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found"));
        return applicationRepository.findByJobRecruiterIdOrderByAppliedAtDesc(recruiter.getId())
                .stream().map(a -> toResponse(a, true)).toList();
    }

    public List<ApplicationResponse> getJobApplications(Long jobId) {
        getOwnedJob(jobId);
        return applicationRepository.findByJobIdOrderByAppliedAtDesc(jobId)
                .stream().map(a -> toResponse(a, true)).toList();
    }

    @Transactional
    public ApplicationResponse updateStatus(Long applicationId, ApplicationStatus status) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        getOwnedJob(app.getJob().getId());

        app.setStatus(status);
        app.setUpdatedAt(Instant.now());
        app = applicationRepository.save(app);

        NotificationType type = switch (status) {
            case ACCEPTED -> NotificationType.APPLICATION_ACCEPTED;
            case REJECTED -> NotificationType.APPLICATION_REJECTED;
            case SHORTLISTED -> NotificationType.APPLICATION_SHORTLISTED;
            default -> NotificationType.GENERAL;
        };

        notificationService.create(
                app.getApplicant().getUser().getId(),
                type,
                "Application Update",
                "Your application for " + app.getJob().getTitle() + " is now " + status.name(),
                app.getId()
        );

        return toResponse(app, true);
    }

    private Job getOwnedJob(Long jobId) {
        User user = securityUtils.getCurrentUser();
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        if (!job.getRecruiter().getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new BadRequestException("Not authorized");
        }
        return job;
    }

    private ApplicationResponse toResponse(Application app, boolean includeApplicant) {
        ApplicationResponse r = new ApplicationResponse();
        r.setId(app.getId());
        r.setJobId(app.getJob().getId());
        r.setJobTitle(app.getJob().getTitle());
        r.setCompanyName(app.getJob().getRecruiter().getCompanyName());
        r.setStatus(app.getStatus());
        r.setAppliedAt(app.getAppliedAt());
        if (includeApplicant) {
            r.setApplicantId(app.getApplicant().getId());
            r.setApplicantName(app.getApplicant().getUser().getName());
            r.setApplicantEmail(app.getApplicant().getUser().getEmail());
            r.setResumeUrl(app.getApplicant().getResumeUrl());
            r.setProfileImageUrl(app.getApplicant().getUser().getProfileImageUrl());
        }
        return r;
    }
}
