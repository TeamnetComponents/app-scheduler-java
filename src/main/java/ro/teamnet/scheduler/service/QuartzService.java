package ro.teamnet.scheduler.service;

import org.quartz.Scheduler;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Scheduling service. Reads stored jobs from the database and passes them to the Quartz scheduler.
 */
@Service
public class QuartzService {

    @Inject
    private Scheduler scheduler;

    @PostConstruct
    private void setupJobs() {
        //TODO: load jobs into the quartz scheduler
    }
}
