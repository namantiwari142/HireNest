package com.hirenest.util;

import com.hirenest.entity.Job;
import com.hirenest.entity.WorkMode;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class JobSpecification {

    private JobSpecification() {}

    public static Specification<Job> filter(
            String keyword,
            String location,
            Double minSalary,
            Double maxSalary,
            String experience,
            String skill,
            WorkMode workMode,
            String company
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isTrue(root.get("active")));

            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern)
                ));
            }
            if (location != null && !location.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%"));
            }
            if (minSalary != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("salaryMax"), minSalary));
            }
            if (maxSalary != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("salaryMin"), maxSalary));
            }
            if (experience != null && !experience.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("experienceRequired")), "%" + experience.toLowerCase() + "%"));
            }
            if (skill != null && !skill.isBlank()) {
                Join<Job, String> skillsJoin = root.join("skills");
                predicates.add(cb.like(cb.lower(skillsJoin), "%" + skill.toLowerCase() + "%"));
            }
            if (workMode != null) {
                predicates.add(cb.equal(root.get("workMode"), workMode));
            }
            if (company != null && !company.isBlank()) {
                Join<Object, Object> recruiter = root.join("recruiter");
                predicates.add(cb.like(cb.lower(recruiter.get("companyName")), "%" + company.toLowerCase() + "%"));
            }
            query.distinct(true);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
