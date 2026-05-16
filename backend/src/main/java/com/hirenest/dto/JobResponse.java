package com.hirenest.dto;

import com.hirenest.entity.JobType;
import com.hirenest.entity.WorkMode;

import java.time.Instant;
import java.util.List;

public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private Double salaryMin;
    private Double salaryMax;
    private String experienceRequired;
    private String location;
    private JobType jobType;
    private WorkMode workMode;
    private List<String> skills;
    private Instant postedAt;
    private String companyName;
    private String companyLogo;
    private Long recruiterId;
    private Long recruiterUserId;
    private String recruiterName;
    private String companyDescription;
    private boolean applied;
    private boolean saved;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getSalaryMin() { return salaryMin; }
    public void setSalaryMin(Double salaryMin) { this.salaryMin = salaryMin; }
    public Double getSalaryMax() { return salaryMax; }
    public void setSalaryMax(Double salaryMax) { this.salaryMax = salaryMax; }
    public String getExperienceRequired() { return experienceRequired; }
    public void setExperienceRequired(String experienceRequired) { this.experienceRequired = experienceRequired; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public JobType getJobType() { return jobType; }
    public void setJobType(JobType jobType) { this.jobType = jobType; }
    public WorkMode getWorkMode() { return workMode; }
    public void setWorkMode(WorkMode workMode) { this.workMode = workMode; }
    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
    public Instant getPostedAt() { return postedAt; }
    public void setPostedAt(Instant postedAt) { this.postedAt = postedAt; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getCompanyLogo() { return companyLogo; }
    public void setCompanyLogo(String companyLogo) { this.companyLogo = companyLogo; }
    public Long getRecruiterId() { return recruiterId; }
    public void setRecruiterId(Long recruiterId) { this.recruiterId = recruiterId; }
    public Long getRecruiterUserId() { return recruiterUserId; }
    public void setRecruiterUserId(Long recruiterUserId) { this.recruiterUserId = recruiterUserId; }
    public String getRecruiterName() { return recruiterName; }
    public void setRecruiterName(String recruiterName) { this.recruiterName = recruiterName; }
    public String getCompanyDescription() { return companyDescription; }
    public void setCompanyDescription(String companyDescription) { this.companyDescription = companyDescription; }
    public boolean isApplied() { return applied; }
    public void setApplied(boolean applied) { this.applied = applied; }
    public boolean isSaved() { return saved; }
    public void setSaved(boolean saved) { this.saved = saved; }
}
