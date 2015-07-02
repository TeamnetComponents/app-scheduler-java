
package ro.teamnet.scheduler.service;


import ro.teamnet.bootstrap.extend.AppPage;
import ro.teamnet.bootstrap.extend.AppPageable;
import ro.teamnet.bootstrap.service.AbstractService;
import ro.teamnet.scheduler.domain.ScheduledJob;
import ro.teamnet.scheduler.domain.ScheduledJobExecution;
import ro.teamnet.scheduler.dto.JobExecutionDTO;
import ro.teamnet.scheduler.enums.JobExecutionStatus;


public interface ScheduledJobExecutionService extends AbstractService<ScheduledJobExecution, Long> {

    void updateExecutionStatus(Long executionId, JobExecutionStatus status);
    ScheduledJob findJobByExecutionId(Long id);
    AppPage<JobExecutionDTO> findJobExecutionDTOs(AppPageable appPageable, Long baseJobId);
}
