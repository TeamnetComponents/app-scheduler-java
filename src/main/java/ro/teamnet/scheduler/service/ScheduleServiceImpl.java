
package ro.teamnet.scheduler.service;


import ro.teamnet.bootstrap.service.AbstractServiceImpl;
import org.springframework.stereotype.Service;

import ro.teamnet.scheduler.domain.Schedule;
import ro.teamnet.scheduler.repository.ScheduleRepository;
import org.springframework.transaction.annotation.Transactional;
import javax.inject.Inject;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ScheduleServiceImpl extends AbstractServiceImpl<Schedule,Long> implements ScheduleService {

    private final Logger log = LoggerFactory.getLogger(ScheduleServiceImpl.class);

    @Inject
    private ScheduleRepository scheduleRepository;

    @Inject
    public ScheduleServiceImpl(ScheduleRepository repository) {
        super(repository);
    }



}
