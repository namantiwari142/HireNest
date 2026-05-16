package com.hirenest.service;

import com.hirenest.dto.*;
import com.hirenest.entity.*;
import com.hirenest.exception.BadRequestException;
import com.hirenest.exception.ResourceNotFoundException;
import com.hirenest.repository.*;
import com.hirenest.util.JobMapper;
import com.hirenest.util.JobSpecification;
import com.hirenest.util.SecurityUtils;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final RecruiterRepository recruiterRepository;
    private final ApplicantRepository applicantRepository;
    private final ApplicationRepository applicationRepository;
    private final SavedJobRepository savedJobRepository;
    private final SecurityUtils securityUtils;
    private final NotificationService notificationService;

    public JobService(
            JobRepository jobRepository,
            RecruiterRepository recruiterRepository,
            ApplicantRepository applicantRepository,
            ApplicationRepository applicationRepository,
            SavedJobRepository savedJobRepository,
            SecurityUtils securityUtils,
            NotificationService notificationService
    ) {
        this.jobRepository = jobRepository;
        this.recruiterRepository = recruiterRepository;
        this.applicantRepository = applicantRepository;
        this.applicationRepository = applicationRepository;
        this.savedJobRepository = savedJobRepository;
        this.securityUtils = securityUtils;
        this.notificationService = notificationService;
    }

    public PageResponse<JobResponse> searchJobs(
            String keyword, String location, Double minSalary, Double maxSalary,
            String experience, String skill, WorkMode workMode, String company,
            String sort, int page, int size, Long applicantUserId
    ) {
        Specification<Job> spec = JobSpecification.filter(keyword, location, minSalary, maxSalary,
                experience, skill, workMode, company);
        Sort sortOrder = resolveSort(sort);
        Page<Job> jobPage = jobRepository.findAll(spec, PageRequest.of(page, size, sortOrder));

        Long applicantId = null;
        if (applicantUserId != null) {
            applicantId = applicantRepository.findByUserId(applicantUserId)
                    .map(Applicant::getId).orElse(null);
        }

        Long finalApplicantId = applicantId;
        List<JobResponse> content = jobPage.getContent().stream()
                .map(job -> enrichJobResponse(job, finalApplicantId))
                .toList();

        return new PageResponse<>(content, page, size, jobPage.getTotalElements(), jobPage.getTotalPages());
    }

    public JobResponse getJob(Long id, Long applicantUserId) {
        Job job = jobRepository.findByIdWithRecruiter(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        Long applicantId = applicantUserId != null
                ? applicantRepository.findByUserId(applicantUserId).map(Applicant::getId).orElse(null)
                : null;
        return enrichJobResponse(job, applicantId);
    }

    public List<JobResponse> getFeatured(int limit) {
        return jobRepository.findAll(PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "postedAt")))
                .getContent().stream().map(j -> enrichJobResponse(j, null)).toList();
    }

    @Transactional
    public JobResponse createJob(JobRequest request) {
        User user = securityUtils.getCurrentUser();
        Recruiter recruiter = recruiterRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException("Recruiter profile not found"));

        Job job = mapRequestToJob(request, new Job());
        job.setRecruiter(recruiter);
        job = jobRepository.save(job);
        return JobMapper.toResponse(job);
    }

    @Transactional
    public JobResponse updateJob(Long id, JobRequest request) {
        Job job = getOwnedJob(id);
        mapRequestToJob(request, job);
        job = jobRepository.save(job);
        return JobMapper.toResponse(job);
    }

    @Transactional
    public void deleteJob(Long id) {
        Job job = getOwnedJob(id);
        job.setActive(false);
        jobRepository.save(job);
    }

    public List<JobResponse> getRecruiterJobs() {
        User user = securityUtils.getCurrentUser();
        Recruiter recruiter = recruiterRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found"));
        return jobRepository.findAll().stream()
                .filter(j -> j.getRecruiter().getId().equals(recruiter.getId()))
                .map(j -> JobMapper.toResponse(j))
                .toList();
    }

    private Job getOwnedJob(Long id) {
        User user = securityUtils.getCurrentUser();
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        if (!job.getRecruiter().getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new BadRequestException("Not authorized to modify this job");
        }
        return job;
    }

    private Job mapRequestToJob(JobRequest request, Job job) {
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        job.setExperienceRequired(request.getExperienceRequired());
        job.setLocation(request.getLocation());
        job.setJobType(request.getJobType());
        job.setWorkMode(request.getWorkMode());
        if (request.getSkills() != null) {
            job.setSkills(request.getSkills());
        }
        return job;
    }

    private JobResponse enrichJobResponse(Job job, Long applicantId) {
        JobResponse r = JobMapper.toResponse(job);
        if (applicantId != null) {
            r.setApplied(applicationRepository.existsByJobIdAndApplicantId(job.getId(), applicantId));
            r.setSaved(savedJobRepository.existsByApplicantIdAndJobId(applicantId, job.getId()));
        }
        return r;
    }

    private Sort resolveSort(String sort) {
        if (sort == null) return Sort.by(Sort.Direction.DESC, "postedAt");
        return switch (sort) {
            case "salary_desc" -> Sort.by(Sort.Direction.DESC, "salaryMax");
            case "salary_asc" -> Sort.by(Sort.Direction.ASC, "salaryMin");
            case "experience" -> Sort.by(Sort.Direction.ASC, "experienceRequired");
            default -> Sort.by(Sort.Direction.DESC, "postedAt");
        };
    }
}
