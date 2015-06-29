
package ro.teamnet.scheduler.service;


import org.springframework.data.domain.Sort;
import ro.teamnet.bootstrap.extend.Filters;
import ro.teamnet.bootstrap.service.AbstractService;
import ro.teamnet.scheduler.domain.ScheduledJob;
import ro.teamnet.scheduler.domain.ScheduledJobExecution;
import ro.teamnet.scheduler.enums.JobExecutionStatus;


public interface ScheduledJobExecutionService extends AbstractService<ScheduledJobExecution, Long> {

    void updateExecutionStatus(Long executionId, JobExecutionStatus status);
    void updateExecutionState(Long executionId, String executionState);
    ScheduledJob findJobByExecutionId(Long id);
    Sort convertDTOSortToEntitySort(Sort dtoSort);
    Filters convertDTOFiltersToEntityFilters(Filters dtoFilters);
}
