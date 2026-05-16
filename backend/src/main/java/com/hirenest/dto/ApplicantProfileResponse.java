package com.hirenest.dto;

import java.util.List;

public class ApplicantProfileResponse {
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String location;
    private String bio;
    private String resumeUrl;
    private String profileImageUrl;
    private List<String> skills;
    private String educationJson;
    private String experienceJson;
    private String projectsJson;
    private int profileCompletion;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getResumeUrl() { return resumeUrl; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
    public String getEducationJson() { return educationJson; }
    public void setEducationJson(String educationJson) { this.educationJson = educationJson; }
    public String getExperienceJson() { return experienceJson; }
    public void setExperienceJson(String experienceJson) { this.experienceJson = experienceJson; }
    public String getProjectsJson() { return projectsJson; }
    public void setProjectsJson(String projectsJson) { this.projectsJson = projectsJson; }
    public int getProfileCompletion() { return profileCompletion; }
    public void setProfileCompletion(int profileCompletion) { this.profileCompletion = profileCompletion; }
}
