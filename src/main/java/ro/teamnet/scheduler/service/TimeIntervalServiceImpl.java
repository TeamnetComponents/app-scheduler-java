package ro.teamnet.scheduler.service;


import org.springframework.stereotype.Service;
import ro.teamnet.bootstrap.service.AbstractServiceImpl;
import ro.teamnet.scheduler.domain.TimeInterval;
import ro.teamnet.scheduler.repository.TimeIntervalRepository;

import javax.inject.Inject;

@Service
public class TimeIntervalServiceImpl extends AbstractServiceImpl<TimeInterval, Long> implements TimeIntervalService {

    @Inject
    public TimeIntervalServiceImpl(TimeIntervalRepository repository) {
        super(repository);
    }
}
