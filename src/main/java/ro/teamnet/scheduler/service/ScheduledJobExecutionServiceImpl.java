
package ro.teamnet.scheduler.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.teamnet.bootstrap.extend.AppPage;
import ro.teamnet.bootstrap.extend.AppPageImpl;
import ro.teamnet.bootstrap.extend.AppPageable;
import ro.teamnet.bootstrap.extend.Filter;
import ro.teamnet.bootstrap.service.AbstractServiceImpl;
import ro.teamnet.scheduler.domain.ScheduledJob;
import ro.teamnet.scheduler.domain.ScheduledJobExecution;
import ro.teamnet.scheduler.dto.JobExecutionDTO;
import ro.teamnet.scheduler.enums.JobExecutionStatus;
import ro.teamnet.scheduler.repository.ScheduledJobExecutionRepository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduledJobExecutionServiceImpl extends AbstractServiceImpl<ScheduledJobExecution, Long> implements ScheduledJobExecutionService {

    @Inject
    public ScheduledJobExecutionServiceImpl(ScheduledJobExecutionRepository repository) {
        super(repository);
    }

    private ScheduledJobExecutionRepository getJobExecutionRepository() {
        return (ScheduledJobExecutionRepository) getRepository();
    }

    @Override
    @Transactional
    public void updateExecutionStatus(Long executionId, JobExecutionStatus status) {
        if (status == null) {
            return;
        }
        ScheduledJobExecution execution = findOne(executionId);
        if (status.equals(execution.getStatus())) {
            return;
        }
        execution.setStatus(status);
        save(execution);
    }

    public ScheduledJob findJobByExecutionId(Long id) {
        return getJobExecutionRepository().findJobByExecutionId(id);
    }

    @Override
    public AppPage<JobExecutionDTO> findJobExecutionDTOs(AppPageable appPageable, Long baseJobId) {
        appPageable.getFilters().addFilter(new Filter("scheduledJob.id", baseJobId.toString(), Filter.FilterType.EQUAL));
        AppPage<ScheduledJobExecution> executions = findAll(appPageable);

        List<JobExecutionDTO> content = new ArrayList<>();
        for (ScheduledJobExecution execution : executions) {
            content.add(execution.toDTO());
        }

        return new AppPageImpl<>(content, appPageable, executions.getTotalElements(),
                appPageable.getFilters());
    }

    @Override
    public AppPage<JobExecutionDTO> findAllJobExecutionDTOs(AppPageable appPageable) {
        AppPage<ScheduledJobExecution> executions = findAll(appPageable);

        List<JobExecutionDTO> content = new ArrayList<>();
        for (ScheduledJobExecution execution : executions) {
            content.add(execution.toDTO());
        }

        return new AppPageImpl<>(content, appPageable, executions.getTotalElements(),
                appPageable.getFilters());
    }

}
