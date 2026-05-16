package com.hirenest.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "applicants")
public class Applicant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String phone;
    private String location;
    private String bio;
    private String resumeUrl;

    @ElementCollection
    @CollectionTable(name = "applicant_skills", joinColumns = @JoinColumn(name = "applicant_id"))
    @Column(name = "skill")
    private List<String> skills = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String educationJson = "[]";

    @Column(columnDefinition = "TEXT")
    private String experienceJson = "[]";

    @Column(columnDefinition = "TEXT")
    private String projectsJson = "[]";

    private int profileCompletion = 20;

    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL)
    private List<Application> applications = new ArrayList<>();

    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL)
    private List<SavedJob> savedJobs = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getResumeUrl() { return resumeUrl; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }
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
    public List<Application> getApplications() { return applications; }
    public void setApplications(List<Application> applications) { this.applications = applications; }
    public List<SavedJob> getSavedJobs() { return savedJobs; }
    public void setSavedJobs(List<SavedJob> savedJobs) { this.savedJobs = savedJobs; }
}
