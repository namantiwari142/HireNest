package com.hirenest.service;

import com.hirenest.dto.DashboardStatsDto;
import com.hirenest.entity.Recruiter;
import com.hirenest.entity.User;
import com.hirenest.exception.ResourceNotFoundException;
import com.hirenest.repository.ApplicationRepository;
import com.hirenest.repository.JobRepository;
import com.hirenest.repository.NotificationRepository;
import com.hirenest.repository.RecruiterRepository;
import com.hirenest.util.SecurityUtils;
import org.springframework.stereotype.Service;

@Service
public class RecruiterService {

    private final RecruiterRepository recruiterRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final NotificationRepository notificationRepository;
    private final SecurityUtils securityUtils;

    public RecruiterService(
            RecruiterRepository recruiterRepository,
            JobRepository jobRepository,
            ApplicationRepository applicationRepository,
            NotificationRepository notificationRepository,
            SecurityUtils securityUtils
    ) {
        this.recruiterRepository = recruiterRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
        this.notificationRepository = notificationRepository;
        this.securityUtils = securityUtils;
    }

    public DashboardStatsDto getDashboardStats() {
        Recruiter recruiter = getRecruiter();
        DashboardStatsDto stats = new DashboardStatsDto();
        stats.setTotalJobs(jobRepository.countByRecruiterId(recruiter.getId()));
        stats.setTotalApplications(applicationRepository.countByJobRecruiterId(recruiter.getId()));
        stats.setUnreadNotifications(notificationRepository.countByUserIdAndReadFalse(recruiter.getUser().getId()));
        return stats;
    }

    private Recruiter getRecruiter() {
        User user = securityUtils.getCurrentUser();
        return recruiterRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter profile not found"));
    }
}
