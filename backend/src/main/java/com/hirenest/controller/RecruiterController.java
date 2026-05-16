package com.hirenest.controller;

import com.hirenest.dto.*;
import com.hirenest.entity.ApplicationStatus;
import com.hirenest.service.ApplicantService;
import com.hirenest.service.ApplicationService;
import com.hirenest.service.JobService;
import com.hirenest.service.RecruiterService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruiter")
public class RecruiterController {

    private final JobService jobService;
    private final ApplicationService applicationService;
    private final RecruiterService recruiterService;
    private final ApplicantService applicantService;

    public RecruiterController(
            JobService jobService,
            ApplicationService applicationService,
            RecruiterService recruiterService,
            ApplicantService applicantService
    ) {
        this.jobService = jobService;
        this.applicationService = applicationService;
        this.recruiterService = recruiterService;
        this.applicantService = applicantService;
    }

    @GetMapping("/applicants/{id}")
    public ResponseEntity<ApiResponse<ApplicantProfileResponse>> viewApplicant(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(applicantService.getProfileById(id)));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> dashboard() {
        return ResponseEntity.ok(ApiResponse.ok(recruiterService.getDashboardStats()));
    }

    @GetMapping("/jobs")
    public ResponseEntity<ApiResponse<List<JobResponse>>> myJobs() {
        return ResponseEntity.ok(ApiResponse.ok(jobService.getRecruiterJobs()));
    }

    @PostMapping("/jobs")
    public ResponseEntity<ApiResponse<JobResponse>> createJob(@Valid @RequestBody JobRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Job posted", jobService.createJob(request)));
    }

    @PutMapping("/jobs/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> updateJob(@PathVariable Long id, @Valid @RequestBody JobRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(jobService.updateJob(id, request)));
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return ResponseEntity.ok(ApiResponse.ok("Job deleted", null));
    }

    @GetMapping("/applications")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> applications() {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.getRecruiterApplications()));
    }

    @GetMapping("/jobs/{jobId}/applications")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> jobApplications(@PathVariable Long jobId) {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.getJobApplications(jobId)));
    }

    @PatchMapping("/applications/{id}/status")
    public ResponseEntity<ApiResponse<ApplicationResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam ApplicationStatus status
    ) {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.updateStatus(id, status)));
    }
}
