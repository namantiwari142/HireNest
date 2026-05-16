package com.hirenest.config;

import com.hirenest.entity.*;
import com.hirenest.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(
            UserRepository userRepository,
            ApplicantRepository applicantRepository,
            RecruiterRepository recruiterRepository,
            JobRepository jobRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (userRepository.count() > 0) return;

            User admin = createUser("admin@hirenest.com", "admin123", "Admin User", Role.ADMIN, passwordEncoder);
            userRepository.save(admin);

            User applicantUser = createUser("applicant@hirenest.com", "applicant123", "Alex Johnson", Role.APPLICANT, passwordEncoder);
            applicantUser = userRepository.save(applicantUser);
            Applicant applicant = new Applicant();
            applicant.setUser(applicantUser);
            applicant.setLocation("Bangalore, India");
            applicant.setSkills(List.of("React", "Java", "Spring Boot"));
            applicant.setProfileCompletion(60);
            applicantRepository.save(applicant);

            User recruiterUser = createUser("recruiter@hirenest.com", "recruiter123", "Sarah Miller", Role.RECRUITER, passwordEncoder);
            recruiterUser = userRepository.save(recruiterUser);
            Recruiter recruiter = new Recruiter();
            recruiter.setUser(recruiterUser);
            recruiter.setCompanyName("TechNova Solutions");
            recruiter.setCompanyLogo("https://ui-avatars.com/api/?name=TN&background=F59E0B&color=fff");
            recruiter.setCompanyDescription("TechNova builds scalable SaaS products for global clients. We value innovation, mentorship, and work-life balance.");
            recruiter.setDesignation("HR Manager");
            recruiter = recruiterRepository.save(recruiter);

            createSampleJob(recruiter, jobRepository, "Senior React Developer",
                    "Build modern UIs with React and TypeScript.", 1200000.0, 1800000.0,
                    "3-5 years", "Bangalore", WorkMode.HYBRID, List.of("React", "TypeScript", "Tailwind"));
            createSampleJob(recruiter, jobRepository, "Java Backend Engineer",
                    "Design REST APIs with Spring Boot and MySQL.", 1500000.0, 2200000.0,
                    "2-4 years", "Remote", WorkMode.REMOTE, List.of("Java", "Spring Boot", "MySQL"));
            createSampleJob(recruiter, jobRepository, "Full Stack Intern",
                    "Learn full-stack development in a fast-paced startup.", 300000.0, 500000.0,
                    "Fresher", "Mumbai", WorkMode.ONSITE, List.of("JavaScript", "Node.js", "MongoDB"));

            Recruiter recruiter2 = new Recruiter();
            User r2 = createUser("hr@cloudscale.io", "recruiter123", "Mike Chen", Role.RECRUITER, passwordEncoder);
            r2 = userRepository.save(r2);
            recruiter2.setUser(r2);
            recruiter2.setCompanyName("CloudScale Inc");
            recruiter2.setCompanyLogo("https://ui-avatars.com/api/?name=CS&background=1E1E2F&color=F59E0B");
            recruiter2.setCompanyDescription("CloudScale helps startups migrate and scale on AWS and Kubernetes with a world-class engineering culture.");
            recruiter2 = recruiterRepository.save(recruiter2);

            createSampleJob(recruiter2, jobRepository, "DevOps Engineer",
                    "Manage CI/CD pipelines and cloud infrastructure.", 1800000.0, 2500000.0,
                    "3+ years", "Hyderabad", WorkMode.HYBRID, List.of("AWS", "Docker", "Kubernetes"));
        };
    }

    private User createUser(String email, String password, String name, Role role, PasswordEncoder encoder) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(encoder.encode(password));
        user.setName(name);
        user.setRole(role);
        user.setProvider(AuthProvider.LOCAL);
        return user;
    }

    private void createSampleJob(Recruiter recruiter, JobRepository repo, String title, String desc,
                                 Double minSal, Double maxSal, String exp, String loc, WorkMode mode, List<String> skills) {
        Job job = new Job();
        job.setRecruiter(recruiter);
        job.setTitle(title);
        job.setDescription(desc);
        job.setSalaryMin(minSal);
        job.setSalaryMax(maxSal);
        job.setExperienceRequired(exp);
        job.setLocation(loc);
        job.setWorkMode(mode);
        job.setJobType(JobType.FULL_TIME);
        job.setSkills(skills);
        repo.save(job);
    }
}
