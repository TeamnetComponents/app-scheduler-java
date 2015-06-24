
package ro.teamnet.scheduler.service;


import ro.teamnet.bootstrap.service.AbstractService;
import ro.teamnet.scheduler.domain.ScheduledJob;
import ro.teamnet.scheduler.domain.ScheduledJobExecution;
import ro.teamnet.scheduler.job.JobExecutionStatus;


public interface ScheduledJobExecutionService extends AbstractService<ScheduledJobExecution,Long>{

    void updateExecutionStatus(Long executionId, JobExecutionStatus status);
    ScheduledJob findJobByExecutionId(Long id);

}
