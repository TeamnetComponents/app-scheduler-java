package ro.teamnet.scheduler.service;

import org.springframework.data.domain.Sort;
import ro.teamnet.bootstrap.extend.*;
import ro.teamnet.scheduler.domain.ScheduledJobExecution;
import ro.teamnet.scheduler.dto.JobExecutionDTO;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * The scheduler service.
 */
public class AppSchedulerServiceImpl implements AppSchedulerService {

    @Inject
    ScheduledJobService scheduledJobService;
    @Inject
    ScheduledJobExecutionService scheduledJobExecutionService;

    @Override
    public AppPage<JobExecutionDTO> findJobExecutions(AppPageable dtoPageable, Long baseJobId) {
        Sort sort = scheduledJobExecutionService.convertDTOSortToEntitySort(dtoPageable.getSort());
        Filters filters = scheduledJobExecutionService.convertDTOFiltersToEntityFilters(dtoPageable.getFilters());
        filters.addFilter(new Filter("scheduledJob.id", baseJobId.toString(), Filter.FilterType.EQUAL));

        AppPageRequest appPageable = new AppPageRequest(dtoPageable.getPageNumber(), dtoPageable.getPageSize(),
                sort, filters, dtoPageable.locale());

        AppPage<ScheduledJobExecution> executions = scheduledJobExecutionService.findAll(appPageable);

        List<JobExecutionDTO> content = new ArrayList<>();
        for (ScheduledJobExecution execution : executions) {
            content.add(execution.toDTO());
        }
        AppPage<JobExecutionDTO> executionDTOs = new AppPageImpl<JobExecutionDTO>(content, dtoPageable,
                executions.getTotalElements(), dtoPageable.getFilters());
        return executionDTOs;
    }
}
