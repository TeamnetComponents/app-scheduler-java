package ro.teamnet.scheduler.service;

import ro.teamnet.scheduler.domain.ScheduledJob;
import ro.teamnet.scheduler.domain.ScheduledJobExecution;
import ro.teamnet.scheduler.dto.JobExecutionDTO;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Oana.Mihai on 6/29/2015.
 */
public class AppSchedulerServiceImpl implements AppSchedulerService {

    @Inject
    ScheduledJobService scheduledJobService;

    @Override
    public Set<JobExecutionDTO> getJobExecutions(Long baseJobId) {
        Set<JobExecutionDTO> jobExecutionDTOs = new HashSet<>();
        ScheduledJob job = scheduledJobService.findOne(baseJobId);
        for (ScheduledJobExecution jobExecution : job.getScheduledJobExecutions()) {
            jobExecutionDTOs.add(jobExecution.toDTO());
        }
        return jobExecutionDTOs;
    }
}
