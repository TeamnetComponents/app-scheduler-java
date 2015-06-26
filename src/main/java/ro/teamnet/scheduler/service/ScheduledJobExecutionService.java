
package ro.teamnet.scheduler.service;


import ro.teamnet.bootstrap.service.AbstractService;
import ro.teamnet.scheduler.domain.ScheduledJobExecution;
import ro.teamnet.scheduler.enums.JobExecutionStatus;


public interface ScheduledJobExecutionService extends AbstractService<ScheduledJobExecution, Long> {

    void updateExecutionStatus(Long executionId, JobExecutionStatus status);

}
