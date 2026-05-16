package com.hirenest.service;

import com.cloudinary.Cloudinary;
import com.hirenest.config.CloudinaryProperties;
import com.hirenest.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class FileUploadService {

    private final Cloudinary cloudinary;
    private final CloudinaryProperties cloudinaryProperties;

    public FileUploadService(Cloudinary cloudinary, CloudinaryProperties cloudinaryProperties) {
        this.cloudinary = cloudinary;
        this.cloudinaryProperties = cloudinaryProperties;
    }

    public String uploadImage(MultipartFile file) {
        ensureConfigured();
        validateImage(file);
        try {
            Map<String, Object> options = new HashMap<>();
            options.put("folder", cloudinaryProperties.getImageFolder());
            options.put("resource_type", "image");
            options.put("use_filename", true);
            options.put("unique_filename", true);

            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), options);
            return extractSecureUrl(result);
        } catch (IOException e) {
            throw new BadRequestException("Failed to upload image: " + e.getMessage());
        }
    }

    public String uploadResume(MultipartFile file, Long applicantId) {
        ensureConfigured();
        validateResume(file);

        try {
            String originalName = file.getOriginalFilename();
            String baseName = "resume";
            if (originalName != null && originalName.contains(".")) {
                baseName = originalName.substring(0, originalName.lastIndexOf('.'))
                        .replaceAll("[^a-zA-Z0-9_-]", "_");
            }

            Map<String, Object> options = new HashMap<>();
            options.put("folder", cloudinaryProperties.getResumeFolder());
            options.put("resource_type", "raw");
            options.put("format", "pdf");
            options.put("public_id", "applicant-" + applicantId + "-" + baseName);
            options.put("use_filename", true);
            options.put("unique_filename", true);
            options.put("overwrite", true);
            options.put("access_mode", "public");

            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), options);
            return extractSecureUrl(result);
        } catch (IOException e) {
            throw new BadRequestException("Failed to upload resume: " + e.getMessage());
        } catch (Exception e) {
            throw new BadRequestException("Cloudinary upload failed: " + e.getMessage());
        }
    }

    private String extractSecureUrl(Map<?, ?> result) {
        Object url = result.get("secure_url");
        if (url == null) {
            url = result.get("url");
        }
        if (url == null) {
            throw new BadRequestException("Upload succeeded but no URL was returned");
        }
        return url.toString();
    }

    private void ensureConfigured() {
        if (!cloudinaryProperties.isConfigured()) {
            throw new BadRequestException(
                    "File upload is not configured. Set CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY, "
                            + "and CLOUDINARY_API_SECRET on the server."
            );
        }
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }
        String contentType = file.getContentType();
        if (contentType != null && !contentType.startsWith("image/")) {
            throw new BadRequestException("Only image files are allowed");
        }
    }

    private void validateResume(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Resume file is empty");
        }

        if (file.getSize() > cloudinaryProperties.getResumeMaxSizeBytes()) {
            throw new BadRequestException(
                    "Resume must be " + cloudinaryProperties.getResumeMaxSizeMb() + "MB or smaller"
            );
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase(Locale.ROOT).endsWith(".pdf")) {
            throw new BadRequestException("Only PDF resumes are allowed (.pdf extension required)");
        }

        String contentType = file.getContentType();
        if (contentType != null
                && !contentType.equalsIgnoreCase("application/pdf")
                && !contentType.equalsIgnoreCase("application/x-pdf")) {
            throw new BadRequestException("Only PDF resumes are allowed");
        }
    }
}
