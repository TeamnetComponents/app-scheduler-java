package ro.teamnet.scheduler.service;


import org.springframework.stereotype.Service;
import ro.teamnet.bootstrap.service.AbstractServiceImpl;
import ro.teamnet.scheduler.domain.TimeInterval;
import ro.teamnet.scheduler.domain.TimeUnit;
import ro.teamnet.scheduler.dto.TimeIntervalDTO;
import ro.teamnet.scheduler.repository.TimeIntervalRepository;

import javax.inject.Inject;
import java.util.List;

@Service
public class TimeIntervalServiceImpl extends AbstractServiceImpl<TimeInterval, Long> implements TimeIntervalService {

    @Inject
    private TimeUnitService timeUnitService;

    @Inject
    public TimeIntervalServiceImpl(TimeIntervalRepository repository) {
        super(repository);
    }

    private TimeIntervalRepository getTimeIntervalRepository() {
        return (TimeIntervalRepository) getRepository();
    }

    @Override
    public TimeInterval findOneOrCreate(TimeIntervalDTO dto) {
        List<TimeInterval> timeIntervals = getTimeIntervalRepository().findAllByIntervalAndTimeUnitCode(
                dto.getInterval(), dto.getTimeUnit());
        if (!timeIntervals.isEmpty()) {
            return timeIntervals.get(0);
        }

        TimeUnit timeUnit = timeUnitService.findByCode(dto.getTimeUnit());
        TimeInterval timeInterval = new TimeInterval();
        timeInterval.setInterval(dto.getInterval());
        timeInterval.setCustom(true);
        timeInterval.setTimeUnit(timeUnit);
        return save(timeInterval);
    }
}