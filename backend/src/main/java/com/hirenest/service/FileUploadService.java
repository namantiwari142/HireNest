package com.hirenest.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.hirenest.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class FileUploadService {

    private final Cloudinary cloudinary;

    public FileUploadService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadImage(MultipartFile file) {
        validateFile(file, "image");
        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", "hirenest/profiles", "resource_type", "image"));
            return result.get("secure_url").toString();
        } catch (IOException e) {
            throw new BadRequestException("Failed to upload image: " + e.getMessage());
        }
    }

    public String uploadResume(MultipartFile file) {
        validateFile(file, "pdf");
        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", "hirenest/resumes", "resource_type", "raw"));
            return result.get("secure_url").toString();
        } catch (IOException e) {
            throw new BadRequestException("Failed to upload resume: " + e.getMessage());
        }
    }

    private void validateFile(MultipartFile file, String type) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }
        if ("pdf".equals(type) && !"application/pdf".equals(file.getContentType())) {
            throw new BadRequestException("Only PDF resumes are allowed");
        }
    }
}
