package com.hirenest.controller;

import com.hirenest.dto.ApiResponse;
import com.hirenest.service.PublicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final PublicService publicService;

    public PublicController(PublicService publicService) {
        this.publicService = publicService;
    }

    @GetMapping("/companies")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> companies() {
        return ResponseEntity.ok(ApiResponse.ok(publicService.getTopCompanies()));
    }

    @GetMapping("/skills")
    public ResponseEntity<ApiResponse<List<String>>> skills() {
        return ResponseEntity.ok(ApiResponse.ok(publicService.getTrendingSkills()));
    }
}
