
package ro.teamnet.scheduler.service;


import ro.teamnet.bootstrap.service.AbstractServiceImpl;
import org.springframework.stereotype.Service;

import ro.teamnet.scheduler.domain.TimeInterval;
import ro.teamnet.scheduler.repository.TimeIntervalRepository;
import org.springframework.transaction.annotation.Transactional;
import javax.inject.Inject;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TimeIntervalServiceImpl extends AbstractServiceImpl<TimeInterval,Long> implements TimeIntervalService {

    private final Logger log = LoggerFactory.getLogger(TimeIntervalServiceImpl.class);

    @Inject
    private TimeIntervalRepository timeIntervalRepository;

    @Inject
    public TimeIntervalServiceImpl(TimeIntervalRepository repository) {
        super(repository);
    }



}
