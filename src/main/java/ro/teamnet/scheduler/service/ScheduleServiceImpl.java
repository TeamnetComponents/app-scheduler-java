package ro.teamnet.scheduler.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ro.teamnet.bootstrap.service.AbstractServiceImpl;
import ro.teamnet.scheduler.domain.Schedule;
import ro.teamnet.scheduler.repository.ScheduleRepository;

import javax.inject.Inject;
import java.util.List;

@Service
public class ScheduleServiceImpl extends AbstractServiceImpl<Schedule, Long> implements ScheduleService {

    private final Logger log = LoggerFactory.getLogger(ScheduleServiceImpl.class);

    @Inject
    private ScheduleRepository scheduleRepository;

    @Inject
    public ScheduleServiceImpl(ScheduleRepository repository) {
        super(repository);
    }


    @Override
    public List<Schedule> findByScheduledJobId(Long scheduledJobId) {
        return scheduleRepository.findByScheduledJobId(scheduledJobId);
    }

    /**
     * Logical delete action.
     *
     * @param id - the element to delete
     */
    @Override
    public void delete(Long id) {
        Schedule schedule = findOne(id);
        schedule.setDeleted(true);
        save(schedule);
    }
}
