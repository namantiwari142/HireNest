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

import java.time.Instant;
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

    @Transactional(readOnly = true)
    public PageResponse<JobResponse> searchJobs(
            String keyword, String location, Double minSalary, Double maxSalary,
            String experience, String skill, WorkMode workMode, String company,
            String sort, int page, int size, Long applicantUserId
    ) {
        Sort sortOrder = resolveSort(sort);
        Page<Job> jobPage;
        if (hasSearchFilters(keyword, location, minSalary, maxSalary, experience, skill, workMode, company)) {
            Specification<Job> spec = JobSpecification.filter(keyword, location, minSalary, maxSalary,
                    experience, skill, workMode, company);
            jobPage = jobRepository.findAll(spec, PageRequest.of(page, size, sortOrder));
        } else {
            jobPage = jobRepository.findBrowsePage(PageRequest.of(page, size, sortOrder));
        }

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

    @Transactional(readOnly = true)
    public JobResponse getJob(Long id, Long applicantUserId) {
        Job job = jobRepository.findByIdWithRecruiter(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        Long applicantId = applicantUserId != null
                ? applicantRepository.findByUserId(applicantUserId).map(Applicant::getId).orElse(null)
                : null;
        return enrichJobResponse(job, applicantId);
    }

    @Transactional(readOnly = true)
    public List<JobResponse> getFeatured(int limit) {
        Sort sort = Sort.by(Sort.Order.desc("postedAt"), Sort.Order.desc("id"));
        return jobRepository.findBrowsePage(PageRequest.of(0, limit, sort))
                .getContent()
                .stream()
                .map(j -> enrichJobResponse(j, null))
                .toList();
    }

    @Transactional
    public JobResponse createJob(JobRequest request) {
        User user = securityUtils.getCurrentUser();
        Recruiter recruiter = recruiterRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException("Recruiter profile not found"));

        Job job = mapRequestToJob(request, new Job());
        job.setRecruiter(recruiter);
        job.setActive(true);
        job.setPostedAt(Instant.now());
        job = jobRepository.save(job);
        return JobMapper.toResponse(
                jobRepository.findByIdWithRecruiter(job.getId()).orElse(job)
        );
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
        jobRepository.delete(job);
    }

    @Transactional(readOnly = true)
    public List<JobResponse> getRecruiterJobs() {
        User user = securityUtils.getCurrentUser();
        Recruiter recruiter = recruiterRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found"));
        return jobRepository.findByRecruiterIdWithDetails(recruiter.getId()).stream()
                .map(JobMapper::toResponse)
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
            job.getSkills().clear();
            job.getSkills().addAll(request.getSkills());
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

    private boolean hasSearchFilters(
            String keyword,
            String location,
            Double minSalary,
            Double maxSalary,
            String experience,
            String skill,
            WorkMode workMode,
            String company
    ) {
        return (keyword != null && !keyword.isBlank())
                || (location != null && !location.isBlank())
                || minSalary != null
                || maxSalary != null
                || (experience != null && !experience.isBlank())
                || (skill != null && !skill.isBlank())
                || workMode != null
                || (company != null && !company.isBlank());
    }

    private Sort resolveSort(String sort) {
        Sort primary = switch (sort != null ? sort : "latest") {
            case "salary_desc" -> Sort.by(Sort.Direction.DESC, "salaryMax");
            case "salary_asc" -> Sort.by(Sort.Direction.ASC, "salaryMin");
            case "experience" -> Sort.by(Sort.Direction.ASC, "experienceRequired");
            default -> Sort.by(Sort.Direction.DESC, "postedAt");
        };
        return primary.and(Sort.by(Sort.Direction.DESC, "id"));
    }
}
