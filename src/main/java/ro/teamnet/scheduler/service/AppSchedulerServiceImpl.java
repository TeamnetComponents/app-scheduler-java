package ro.teamnet.scheduler.service;

import org.springframework.stereotype.Service;
import ro.teamnet.bootstrap.extend.AppPage;
import ro.teamnet.bootstrap.extend.AppPageable;
import ro.teamnet.scheduler.dto.JobExecutionDTO;

import javax.inject.Inject;

/**
 * The scheduler service.
 */
@Service
public class AppSchedulerServiceImpl implements AppSchedulerService {

    @Inject
    ScheduledJobExecutionService scheduledJobExecutionService;

    @Override
    public AppPage<JobExecutionDTO> findJobExecutions(AppPageable dtoPageable, Long baseJobId) {
        return scheduledJobExecutionService.findJobExecutionDTOs(dtoPageable, baseJobId);
    }
}
