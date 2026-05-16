package com.hirenest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.upload")
public class UploadProperties {

    /** cloudinary | local */
    private String provider = "cloudinary";

    /** Directory on disk when provider=local (relative or absolute) */
    private String directory = "uploads";

    /** Public backend URL for local file links, e.g. https://hirenest-cdb2.onrender.com */
    private String publicBaseUrl = "http://localhost:8080";

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public boolean isLocal() {
        return "local".equalsIgnoreCase(provider);
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getPublicBaseUrl() {
        return publicBaseUrl == null ? "" : publicBaseUrl.replaceAll("/+$", "");
    }

    public void setPublicBaseUrl(String publicBaseUrl) {
        this.publicBaseUrl = publicBaseUrl;
    }
}
