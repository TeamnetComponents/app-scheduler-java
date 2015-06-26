
package ro.teamnet.scheduler.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ro.teamnet.bootstrap.service.AbstractServiceImpl;
import ro.teamnet.scheduler.domain.ScheduledJob;
import ro.teamnet.scheduler.domain.ScheduledJobExecution;
import ro.teamnet.scheduler.enums.JobExecutionStatus;
import ro.teamnet.scheduler.repository.ScheduledJobExecutionRepository;

import javax.inject.Inject;

@Service
public class ScheduledJobExecutionServiceImpl extends AbstractServiceImpl<ScheduledJobExecution,Long> implements ScheduledJobExecutionService {

    private final Logger log = LoggerFactory.getLogger(ScheduledJobExecutionServiceImpl.class);

    @Inject
    private ScheduledJobExecutionRepository scheduledJobExecutionRepository;

    @Inject
    public ScheduledJobExecutionServiceImpl(ScheduledJobExecutionRepository repository) {
        super(repository);
    }


    @Override
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

    @Override
    public void updateExecutionState(Long executionId, String executionState) {
        if (executionState == null) {
            return;
        }
        ScheduledJobExecution execution = findOne(executionId);
        if (executionState.equals(execution.getState())) {
            return;
        }
        execution.setState(executionState);
        save(execution);
    }

    public ScheduledJob findJobByExecutionId(Long id) {
        return scheduledJobExecutionRepository.findJobByExecutionId(id);
    }
}
