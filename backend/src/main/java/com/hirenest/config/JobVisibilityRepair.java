package com.hirenest.config;

import com.hirenest.entity.Job;
import com.hirenest.repository.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Fixes jobs stored with active=false when the column was added without a proper default
 * (common with Hibernate ddl-auto on MySQL). Does not run for jobs that were hard-deleted.
 */
@Component
public class JobVisibilityRepair {

    private static final Logger log = LoggerFactory.getLogger(JobVisibilityRepair.class);

    private final JobRepository jobRepository;

    public JobVisibilityRepair(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void repairInactiveJobs() {
        List<Job> inactive = jobRepository.findByActiveFalse();
        if (inactive.isEmpty()) {
            return;
        }
        inactive.forEach(job -> job.setActive(true));
        jobRepository.saveAll(inactive);
        log.info("Re-activated {} job(s) for applicant browse (legacy active flag fix)", inactive.size());
    }
}
