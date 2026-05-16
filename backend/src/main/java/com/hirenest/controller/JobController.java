package com.hirenest.controller;

import com.hirenest.dto.ApiResponse;
import com.hirenest.dto.JobResponse;
import com.hirenest.dto.PageResponse;
import com.hirenest.entity.WorkMode;
import com.hirenest.service.JobService;
import com.hirenest.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;
    private final SecurityUtils securityUtils;

    public JobController(JobService jobService, SecurityUtils securityUtils) {
        this.jobService = jobService;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<JobResponse>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double minSalary,
            @RequestParam(required = false) Double maxSalary,
            @RequestParam(required = false) String experience,
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) WorkMode workMode,
            @RequestParam(required = false) String company,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Authentication auth
    ) {
        Long userId = getUserIdIfAuthenticated(auth);
        return ResponseEntity.ok(ApiResponse.ok(
                jobService.searchJobs(keyword, location, minSalary, maxSalary, experience, skill,
                        workMode, company, sort, page, size, userId)));
    }

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<JobResponse>>> featured(
            @RequestParam(defaultValue = "12") int limit
    ) {
        return ResponseEntity.ok(ApiResponse.ok(jobService.getFeatured(Math.min(limit, 50))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> getJob(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(jobService.getJob(id, getUserIdIfAuthenticated(auth))));
    }

    private Long getUserIdIfAuthenticated(Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        try {
            return securityUtils.getCurrentUser().getId();
        } catch (Exception e) {
            return null;
        }
    }
}
