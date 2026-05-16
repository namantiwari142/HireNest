package com.hirenest.dto;

import java.util.List;

public class ApplicantProfileRequest {
    private String phone;
    private String location;
    private String bio;
    private List<String> skills;
    private String educationJson;
    private String experienceJson;
    private String projectsJson;

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
    public String getEducationJson() { return educationJson; }
    public void setEducationJson(String educationJson) { this.educationJson = educationJson; }
    public String getExperienceJson() { return experienceJson; }
    public void setExperienceJson(String experienceJson) { this.experienceJson = experienceJson; }
    public String getProjectsJson() { return projectsJson; }
    public void setProjectsJson(String projectsJson) { this.projectsJson = projectsJson; }
}
