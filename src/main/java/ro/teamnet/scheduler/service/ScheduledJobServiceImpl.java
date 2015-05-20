package ro.teamnet.scheduler.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ro.teamnet.bootstrap.service.AbstractServiceImpl;
import ro.teamnet.scheduler.domain.ScheduledJob;
import ro.teamnet.scheduler.repository.ScheduledJobRepository;

import javax.inject.Inject;

@Service
public class ScheduledJobServiceImpl extends AbstractServiceImpl<ScheduledJob, Long> implements ScheduledJobService {

    private final Logger log = LoggerFactory.getLogger(ScheduledJobServiceImpl.class);

    @Inject
    private ScheduledJobRepository scheduledJobRepository;

    @Inject
    public ScheduledJobServiceImpl(ScheduledJobRepository repository) {
        super(repository);
    }


}
