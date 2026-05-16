package com.hirenest.controller;

import com.hirenest.dto.*;
import com.hirenest.service.*;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/applicant")
public class ApplicantController {

    private final ApplicantService applicantService;
    private final ApplicationService applicationService;
    private final SavedJobService savedJobService;

    public ApplicantController(
            ApplicantService applicantService,
            ApplicationService applicationService,
            SavedJobService savedJobService
    ) {
        this.applicantService = applicantService;
        this.applicationService = applicationService;
        this.savedJobService = savedJobService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> dashboard() {
        return ResponseEntity.ok(ApiResponse.ok(applicantService.getDashboardStats()));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<ApplicantProfileResponse>> profile() {
        return ResponseEntity.ok(ApiResponse.ok(applicantService.getProfile()));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<ApplicantProfileResponse>> updateProfile(@RequestBody ApplicantProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(applicantService.updateProfile(request)));
    }

    @PostMapping(value = "/profile/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ApplicantProfileResponse>> uploadResume(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.ok("Resume uploaded", applicantService.uploadResume(file)));
    }

    @PostMapping(value = "/profile/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ApplicantProfileResponse>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.ok(applicantService.uploadProfileImage(file)));
    }

    @PostMapping("/jobs/{jobId}/apply")
    public ResponseEntity<ApiResponse<ApplicationResponse>> apply(@PathVariable Long jobId) {
        return ResponseEntity.ok(ApiResponse.ok("Applied successfully", applicationService.apply(jobId)));
    }

    @GetMapping("/applications")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> applications() {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.getMyApplications()));
    }

    @PostMapping("/jobs/{jobId}/save")
    public ResponseEntity<ApiResponse<Void>> toggleSave(@PathVariable Long jobId) {
        savedJobService.toggleSave(jobId);
        return ResponseEntity.ok(ApiResponse.ok("Saved status updated", null));
    }

    @GetMapping("/saved-jobs")
    public ResponseEntity<ApiResponse<List<JobResponse>>> savedJobs() {
        return ResponseEntity.ok(ApiResponse.ok(savedJobService.getSavedJobs()));
    }
}
