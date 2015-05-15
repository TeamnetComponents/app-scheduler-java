package ro.teamnet.scheduler.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ro.teamnet.bootstrap.service.AbstractServiceImpl;
import ro.teamnet.scheduler.domain.SchedulableJob;
import ro.teamnet.scheduler.repository.SchedulableJobRepository;

import javax.inject.Inject;

@Service
public class SchedulableJobServiceImpl extends AbstractServiceImpl<SchedulableJob, Long> implements SchedulableJobService {

    private final Logger log = LoggerFactory.getLogger(SchedulableJobServiceImpl.class);

    @Inject
    private SchedulableJobRepository schedulableJobRepository;

    @Inject
    public SchedulableJobServiceImpl(SchedulableJobRepository repository) {
        super(repository);
    }


}
