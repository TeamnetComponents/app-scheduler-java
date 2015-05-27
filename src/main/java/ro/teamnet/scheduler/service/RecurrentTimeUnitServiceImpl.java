package ro.teamnet.scheduler.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ro.teamnet.bootstrap.service.AbstractServiceImpl;
import ro.teamnet.scheduler.domain.RecurrentTimeUnit;
import ro.teamnet.scheduler.domain.Schedule;
import ro.teamnet.scheduler.domain.TimeUnit;
import ro.teamnet.scheduler.repository.RecurrentTimeUnitRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

@Service
public class RecurrentTimeUnitServiceImpl extends AbstractServiceImpl<RecurrentTimeUnit, Long> implements RecurrentTimeUnitService {

    private final Logger log = LoggerFactory.getLogger(RecurrentTimeUnitServiceImpl.class);

    @Inject
    private RecurrentTimeUnitRepository recurrentTimeUnitRepository;

    @Inject
    public RecurrentTimeUnitServiceImpl(RecurrentTimeUnitRepository repository) {
        super(repository);
    }

    @Override
    public RecurrentTimeUnit findByTimeUnitAndValueAndSchedule(TimeUnit timeUnit, Integer value, Schedule schedule) {
        return recurrentTimeUnitRepository.findByTimeUnitAndValueAndSchedule(timeUnit, value, schedule);
    }

    @Override
    public Set<RecurrentTimeUnit> findByScheduleAndTimeUnit(Schedule schedule, TimeUnit timeUnit) {
        return recurrentTimeUnitRepository.findByScheduleAndTimeUnit(schedule, timeUnit);
    }
}
