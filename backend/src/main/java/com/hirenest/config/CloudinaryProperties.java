package com.hirenest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cloudinary")
public class CloudinaryProperties {

    private String cloudName = "";
    private String apiKey = "";
    private String apiSecret = "";
    private String resumeFolder = "hirenest/resumes";
    private String imageFolder = "hirenest/profiles";
    private int resumeMaxSizeMb = 5;

    public String getCloudName() {
        return cloudName;
    }

    public void setCloudName(String cloudName) {
        this.cloudName = cloudName;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    public String getResumeFolder() {
        return resumeFolder;
    }

    public void setResumeFolder(String resumeFolder) {
        this.resumeFolder = resumeFolder;
    }

    public String getImageFolder() {
        return imageFolder;
    }

    public void setImageFolder(String imageFolder) {
        this.imageFolder = imageFolder;
    }

    public int getResumeMaxSizeMb() {
        return resumeMaxSizeMb;
    }

    public void setResumeMaxSizeMb(int resumeMaxSizeMb) {
        this.resumeMaxSizeMb = resumeMaxSizeMb;
    }

    public long getResumeMaxSizeBytes() {
        return (long) resumeMaxSizeMb * 1024 * 1024;
    }

    public boolean isConfigured() {
        return cloudName != null && !cloudName.isBlank()
                && apiKey != null && !apiKey.isBlank()
                && apiSecret != null && !apiSecret.isBlank()
                && !cloudName.startsWith("your-")
                && !apiKey.startsWith("your-");
    }
}
