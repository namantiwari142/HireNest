package com.hirenest.dto;

import com.hirenest.entity.JobType;
import com.hirenest.entity.WorkMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class JobRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    private Double salaryMin;
    private Double salaryMax;
    private String experienceRequired;
    private String location;

    @NotNull
    private JobType jobType;

    @NotNull
    private WorkMode workMode;

    private List<String> skills;

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
}
