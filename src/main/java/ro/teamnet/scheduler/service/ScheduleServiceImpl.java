package ro.teamnet.scheduler.service;


import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ro.teamnet.bootstrap.extend.AppPage;
import ro.teamnet.bootstrap.extend.AppPageable;
import ro.teamnet.bootstrap.extend.Filter;
import ro.teamnet.bootstrap.service.AbstractServiceImpl;
import ro.teamnet.scheduler.domain.Schedule;
import ro.teamnet.scheduler.domain.TimeInterval;
import ro.teamnet.scheduler.repository.ScheduleRepository;

import javax.inject.Inject;
import java.util.List;

@Service
public class ScheduleServiceImpl extends AbstractServiceImpl<Schedule, Long> implements ScheduleService {

    private final Logger log = LoggerFactory.getLogger(ScheduleServiceImpl.class);

    @Inject
    private ScheduleRepository scheduleRepository;

    @Inject
    private CronExpressionService cronExpressionService;

    @Inject
    public ScheduleServiceImpl(ScheduleRepository repository) {
        super(repository);
    }


    @Override
    public List<Schedule> findByScheduledJobId(Long scheduledJobId) {
        return scheduleRepository.findByDeletedFalseAndScheduledJobId(scheduledJobId);
    }

    @Override
    public Schedule findByStartDate(DateTime startDate) {
        return scheduleRepository.findByStartTime(startDate);
    }

    @Override
    public Schedule findByStartTimeAndRecurrent(DateTime startTime, Boolean recurrent) {
        return scheduleRepository.findByStartTimeAndRecurrent(startTime, recurrent);
    }

    @Override
    public Schedule findByStartTimeAndRecurrentAndTimeInterval(DateTime startTime, Boolean recurrent, TimeInterval timeInterval) {
        return scheduleRepository.findByStartTimeAndRecurrentAndTimeInterval(startTime, recurrent, timeInterval);
    }

    @Override
    public Schedule findByIdAndStartTimeAndRecurrent(Long id, DateTime startTime, Boolean recurrent) {
        return scheduleRepository.findByIdAndStartTimeAndRecurrent(id, startTime, recurrent);
    }

    @Override
    public Schedule findByStartTimeAndRecurrentAndLastUpdated(DateTime startTime, Boolean recurrent, DateTime lastUpdated) {
        return scheduleRepository.findByStartTimeAndRecurrentAndLastUpdated(startTime, recurrent, lastUpdated);
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

    @Override
    public List<Schedule> findAll() {
        return scheduleRepository.findByDeletedFalse();
    }

    @Override
    public AppPage<Schedule> findAll(AppPageable appPageable) {
        appPageable.getFilters().addFilter(new Filter("deleted", Boolean.FALSE.toString(), Filter.FilterType.EQUAL));
        return super.findAll(appPageable);
    }

    @Override
    public Schedule save(Schedule schedule) {
        schedule.setCron(getCronExpression(schedule));
        return super.save(schedule);
    }

    private String getCronExpression(Schedule schedule) {
        String cronExpression = "";
        if (!schedule.getRecurrent()) {
            cronExpression = cronExpressionService.buildCronExpressionForRecurrentFalse(schedule);
        } else {
            if (schedule.getTimeInterval() != null) {
                cronExpression = cronExpressionService.buildCronExpressionForRecurrentTrueWithTimeInterval(schedule);
            } else {
                cronExpression = cronExpressionService.buildCronExpressionForRecurrentTrueWithRecurrentTimeUnit(schedule);
            }
        }
        return cronExpression;
    }
}
