package com.hirenest.service;

import com.cloudinary.Cloudinary;
import com.hirenest.config.CloudinaryProperties;
import com.hirenest.config.UploadProperties;
import com.hirenest.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class FileUploadService {

    private static final Logger log = LoggerFactory.getLogger(FileUploadService.class);

    private final Cloudinary cloudinary;
    private final CloudinaryProperties cloudinaryProperties;
    private final UploadProperties uploadProperties;

    public FileUploadService(
            Cloudinary cloudinary,
            CloudinaryProperties cloudinaryProperties,
            UploadProperties uploadProperties
    ) {
        this.cloudinary = cloudinary;
        this.cloudinaryProperties = cloudinaryProperties;
        this.uploadProperties = uploadProperties;
    }

    public String uploadImage(MultipartFile file) {
        validateImage(file);
        if (uploadProperties.isLocal()) {
            return storeLocally(file, "profiles", null);
        }
        ensureCloudinaryConfigured();
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
        } catch (Exception e) {
            throw mapCloudinaryError(e);
        }
    }

    public String uploadResume(MultipartFile file, Long applicantId) {
        validateResume(file);
        if (uploadProperties.isLocal()) {
            return storeLocally(file, "resumes", applicantId);
        }
        ensureCloudinaryConfigured();
        try {
            Map<String, Object> options = new HashMap<>();
            options.put("folder", cloudinaryProperties.getResumeFolder());
            options.put("resource_type", "raw");
            options.put("use_filename", true);
            options.put("unique_filename", true);
            options.put("access_mode", "public");

            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), options);
            return extractSecureUrl(result);
        } catch (IOException e) {
            throw new BadRequestException("Failed to upload resume: " + e.getMessage());
        } catch (Exception e) {
            throw mapCloudinaryError(e);
        }
    }

    private String storeLocally(MultipartFile file, String subfolder, Long applicantId) {
        try {
            String original = file.getOriginalFilename();
            String ext = ".pdf";
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf('.')).toLowerCase(Locale.ROOT);
            }
            String prefix = applicantId != null ? "applicant-" + applicantId + "-" : "";
            String filename = prefix + UUID.randomUUID() + ext;

            Path dir = Path.of(uploadProperties.getDirectory(), subfolder).toAbsolutePath();
            Files.createDirectories(dir);
            Path target = dir.resolve(filename);
            Files.write(target, file.getBytes());

            String url = uploadProperties.getPublicBaseUrl() + "/uploads/" + subfolder + "/" + filename;
            log.info("Stored upload locally: {}", url);
            return url;
        } catch (IOException e) {
            throw new BadRequestException("Failed to save file: " + e.getMessage());
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

    private void ensureCloudinaryConfigured() {
        if (!cloudinaryProperties.isConfigured()) {
            throw new BadRequestException(
                    "Cloudinary is not configured on the server. Set CLOUDINARY_CLOUD_NAME, "
                            + "CLOUDINARY_API_KEY, and CLOUDINARY_API_SECRET on Render, "
                            + "or set UPLOAD_PROVIDER=local to store files on the server temporarily."
            );
        }
    }

    private BadRequestException mapCloudinaryError(Exception e) {
        String message = e.getMessage() != null ? e.getMessage() : e.toString();
        log.error("Cloudinary upload failed: {}", message);

        if (message.toLowerCase(Locale.ROOT).contains("cloud_name is disabled")
                || message.toLowerCase(Locale.ROOT).contains("cloud_name disabled")) {
            return new BadRequestException(
                    "Cloudinary account is disabled. Verify your email at cloudinary.com, "
                            + "then set correct CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY, and "
                            + "CLOUDINARY_API_SECRET on Render. Or set UPLOAD_PROVIDER=local on Render "
                            + "until Cloudinary is active."
            );
        }
        if (message.toLowerCase(Locale.ROOT).contains("invalid cloud name")
                || message.toLowerCase(Locale.ROOT).contains("unknown api key")) {
            return new BadRequestException(
                    "Invalid Cloudinary credentials. Copy cloud name, API key, and API secret "
                            + "from cloudinary.com/console and update Render environment variables."
            );
        }
        return new BadRequestException("Cloudinary upload failed: " + message);
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
