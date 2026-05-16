package com.hirenest.service;

import com.hirenest.dto.ApplicantProfileRequest;
import com.hirenest.dto.ApplicantProfileResponse;
import com.hirenest.dto.DashboardStatsDto;
import com.hirenest.entity.Applicant;
import com.hirenest.entity.User;
import com.hirenest.exception.ResourceNotFoundException;
import com.hirenest.repository.ApplicantRepository;
import com.hirenest.repository.ApplicationRepository;
import com.hirenest.repository.NotificationRepository;
import com.hirenest.repository.SavedJobRepository;
import com.hirenest.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ApplicantService {

    private final ApplicantRepository applicantRepository;
    private final ApplicationRepository applicationRepository;
    private final SavedJobRepository savedJobRepository;
    private final NotificationRepository notificationRepository;
    private final SecurityUtils securityUtils;
    private final FileUploadService fileUploadService;
    private final UserService userService;

    public ApplicantService(
            ApplicantRepository applicantRepository,
            ApplicationRepository applicationRepository,
            SavedJobRepository savedJobRepository,
            NotificationRepository notificationRepository,
            SecurityUtils securityUtils,
            FileUploadService fileUploadService,
            UserService userService
    ) {
        this.applicantRepository = applicantRepository;
        this.applicationRepository = applicationRepository;
        this.savedJobRepository = savedJobRepository;
        this.notificationRepository = notificationRepository;
        this.securityUtils = securityUtils;
        this.fileUploadService = fileUploadService;
        this.userService = userService;
    }

    public ApplicantProfileResponse getProfile() {
        return toResponse(getApplicant());
    }

    public ApplicantProfileResponse getProfileById(Long id) {
        Applicant applicant = applicantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Applicant not found"));
        return toResponse(applicant);
    }

    @Transactional
    public ApplicantProfileResponse updateProfile(ApplicantProfileRequest request) {
        Applicant applicant = getApplicant();
        if (request.getPhone() != null) applicant.setPhone(request.getPhone());
        if (request.getLocation() != null) applicant.setLocation(request.getLocation());
        if (request.getBio() != null) applicant.setBio(request.getBio());
        if (request.getSkills() != null) applicant.setSkills(request.getSkills());
        if (request.getEducationJson() != null) applicant.setEducationJson(request.getEducationJson());
        if (request.getExperienceJson() != null) applicant.setExperienceJson(request.getExperienceJson());
        if (request.getProjectsJson() != null) applicant.setProjectsJson(request.getProjectsJson());
        applicant.setProfileCompletion(calculateCompletion(applicant));
        applicant = applicantRepository.save(applicant);
        return toResponse(applicant);
    }

    @Transactional
    public ApplicantProfileResponse uploadResume(MultipartFile file) {
        Applicant applicant = getApplicant();
        applicant.setResumeUrl(fileUploadService.uploadResume(file));
        applicant.setProfileCompletion(calculateCompletion(applicant));
        applicant = applicantRepository.save(applicant);
        return toResponse(applicant);
    }

    @Transactional
    public ApplicantProfileResponse uploadProfileImage(MultipartFile file) {
        Applicant applicant = getApplicant();
        String url = fileUploadService.uploadImage(file);
        userService.updateProfileImage(applicant.getUser().getId(), url);
        applicant.setProfileCompletion(calculateCompletion(applicant));
        applicant = applicantRepository.save(applicant);
        return toResponse(applicantRepository.findById(applicant.getId()).orElse(applicant));
    }

    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats() {
        Applicant applicant = getApplicant();
        DashboardStatsDto stats = new DashboardStatsDto();
        stats.setTotalApplications(applicationRepository.findByApplicantIdOrderByAppliedAtDesc(applicant.getId()).size());
        stats.setSavedJobs(savedJobRepository.findByApplicantIdOrderBySavedAtDesc(applicant.getId()).size());
        stats.setUnreadNotifications(notificationRepository.countByUserIdAndReadFalse(applicant.getUser().getId()));
        stats.setProfileCompletion(applicant.getProfileCompletion());
        return stats;
    }

    private Applicant getApplicant() {
        User user = securityUtils.getCurrentUser();
        return applicantRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Applicant profile not found"));
    }

    private int calculateCompletion(Applicant a) {
        int score = 20;
        if (a.getPhone() != null && !a.getPhone().isBlank()) score += 10;
        if (a.getLocation() != null && !a.getLocation().isBlank()) score += 10;
        if (a.getBio() != null && !a.getBio().isBlank()) score += 15;
        if (a.getResumeUrl() != null) score += 20;
        if (a.getSkills() != null && !a.getSkills().isEmpty()) score += 15;
        if (a.getUser().getProfileImageUrl() != null) score += 10;
        return Math.min(score, 100);
    }

    private ApplicantProfileResponse toResponse(Applicant a) {
        ApplicantProfileResponse r = new ApplicantProfileResponse();
        r.setId(a.getId());
        r.setUserId(a.getUser().getId());
        r.setName(a.getUser().getName());
        r.setEmail(a.getUser().getEmail());
        r.setPhone(a.getPhone());
        r.setLocation(a.getLocation());
        r.setBio(a.getBio());
        r.setResumeUrl(a.getResumeUrl());
        r.setProfileImageUrl(a.getUser().getProfileImageUrl());
        r.setSkills(a.getSkills());
        r.setEducationJson(a.getEducationJson());
        r.setExperienceJson(a.getExperienceJson());
        r.setProjectsJson(a.getProjectsJson());
        r.setProfileCompletion(a.getProfileCompletion());
        return r;
    }
}
