package com.hirenest.util;

import com.hirenest.dto.JobResponse;
import com.hirenest.entity.Job;

public final class JobMapper {

    private JobMapper() {}

    public static JobResponse toResponse(Job job) {
        JobResponse r = new JobResponse();
        r.setId(job.getId());
        r.setTitle(job.getTitle());
        r.setDescription(job.getDescription());
        r.setSalaryMin(job.getSalaryMin());
        r.setSalaryMax(job.getSalaryMax());
        r.setExperienceRequired(job.getExperienceRequired());
        r.setLocation(job.getLocation());
        r.setJobType(job.getJobType());
        r.setWorkMode(job.getWorkMode());
        r.setSkills(job.getSkills());
        r.setPostedAt(job.getPostedAt());
        if (job.getRecruiter() != null) {
            r.setRecruiterId(job.getRecruiter().getId());
            r.setCompanyName(job.getRecruiter().getCompanyName());
            r.setCompanyLogo(job.getRecruiter().getCompanyLogo());
            r.setCompanyDescription(job.getRecruiter().getCompanyDescription());
            if (job.getRecruiter().getUser() != null) {
                r.setRecruiterUserId(job.getRecruiter().getUser().getId());
                r.setRecruiterName(job.getRecruiter().getUser().getName());
            }
        }
        return r;
    }
}
