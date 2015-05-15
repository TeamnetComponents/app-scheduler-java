package ro.teamnet.scheduler.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ro.teamnet.bootstrap.service.AbstractServiceImpl;
import ro.teamnet.scheduler.domain.Schedule;
import ro.teamnet.scheduler.repository.ScheduleRepository;

import javax.inject.Inject;

@Service
public class ScheduleServiceImpl extends AbstractServiceImpl<Schedule, Long> implements ScheduleService {

    private final Logger log = LoggerFactory.getLogger(ScheduleServiceImpl.class);

    @Inject
    private ScheduleRepository scheduleRepository;

    @Inject
    public ScheduleServiceImpl(ScheduleRepository repository) {
        super(repository);
    }


}
