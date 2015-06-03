
package ro.teamnet.scheduler.service;


import ro.teamnet.bootstrap.service.AbstractServiceImpl;
import org.springframework.stereotype.Service;

import ro.teamnet.scheduler.domain.ScheduledJobExecution;
import ro.teamnet.scheduler.repository.ScheduledJobExecutionRepository;
import org.springframework.transaction.annotation.Transactional;
import javax.inject.Inject;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ScheduledJobExecutionServiceImpl extends AbstractServiceImpl<ScheduledJobExecution,Long> implements ScheduledJobExecutionService {

    private final Logger log = LoggerFactory.getLogger(ScheduledJobExecutionServiceImpl.class);

    @Inject
    private ScheduledJobExecutionRepository scheduledJobExecutionRepository;

    @Inject
    public ScheduledJobExecutionServiceImpl(ScheduledJobExecutionRepository repository) {
        super(repository);
    }



}
