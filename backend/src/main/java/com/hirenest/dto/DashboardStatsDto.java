package com.hirenest.dto;

public class DashboardStatsDto {
    private long totalJobs;
    private long totalApplications;
    private long savedJobs;
    private long unreadNotifications;
    private int profileCompletion;

    public long getTotalJobs() { return totalJobs; }
    public void setTotalJobs(long totalJobs) { this.totalJobs = totalJobs; }
    public long getTotalApplications() { return totalApplications; }
    public void setTotalApplications(long totalApplications) { this.totalApplications = totalApplications; }
    public long getSavedJobs() { return savedJobs; }
    public void setSavedJobs(long savedJobs) { this.savedJobs = savedJobs; }
    public long getUnreadNotifications() { return unreadNotifications; }
    public void setUnreadNotifications(long unreadNotifications) { this.unreadNotifications = unreadNotifications; }
    public int getProfileCompletion() { return profileCompletion; }
    public void setProfileCompletion(int profileCompletion) { this.profileCompletion = profileCompletion; }
}
