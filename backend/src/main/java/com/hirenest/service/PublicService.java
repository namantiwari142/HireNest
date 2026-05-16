package com.hirenest.service;

import com.hirenest.repository.RecruiterRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PublicService {

    private final RecruiterRepository recruiterRepository;

    public PublicService(RecruiterRepository recruiterRepository) {
        this.recruiterRepository = recruiterRepository;
    }

    public List<Map<String, String>> getTopCompanies() {
        return recruiterRepository.findAll().stream()
                .limit(8)
                .map(r -> {
                    Map<String, String> m = new HashMap<>();
                    m.put("name", r.getCompanyName());
                    m.put("logo", r.getCompanyLogo() != null ? r.getCompanyLogo() :
                            "https://ui-avatars.com/api/?name=" + r.getCompanyName().charAt(0));
                    return m;
                })
                .toList();
    }

    public List<String> getTrendingSkills() {
        return List.of("React", "Java", "Spring Boot", "Python", "AWS", "Node.js", "SQL", "Docker");
    }
}
