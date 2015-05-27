package ro.teamnet.scheduler.service;


import ro.teamnet.bootstrap.service.AbstractService;
import ro.teamnet.scheduler.domain.RecurrentTimeUnit;
import ro.teamnet.scheduler.domain.Schedule;
import ro.teamnet.scheduler.domain.TimeUnit;

import java.util.List;
import java.util.Set;


public interface RecurrentTimeUnitService extends AbstractService<RecurrentTimeUnit, Long> {

    RecurrentTimeUnit findByTimeUnitAndValueAndSchedule(TimeUnit timeUnit, Integer value, Schedule schedule);
    Set<RecurrentTimeUnit> findByScheduleAndTimeUnit(Schedule schedule, TimeUnit timeUnit);
}
