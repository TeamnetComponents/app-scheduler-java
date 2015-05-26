package ro.teamnet.cron.test;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ro.teamnet.SchedulerTestApplication;
import ro.teamnet.scheduler.domain.RecurrentTimeUnit;
import ro.teamnet.scheduler.domain.Schedule;
import ro.teamnet.scheduler.domain.TimeInterval;
import ro.teamnet.scheduler.domain.TimeUnit;
import ro.teamnet.scheduler.repository.ScheduleRepository;
import ro.teamnet.scheduler.service.*;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SchedulerTestApplication.class)
@WebAppConfiguration
@IntegrationTest
@ActiveProfiles("test-scheduler")
public class CronExpressionTest {

    @Inject
    private CronExpressionService cronExpressionService;

    @Inject
    private ScheduleService scheduleService;

    @Inject
    private TimeUnitService timeUnitService;

    @Inject
    private RecurrentTimeUnitService recurrentTimeUnitService;

    @Inject
    private TimeIntervalService timeIntervalService;

    private Logger log = LoggerFactory.getLogger(CronExpressionTest.class);

    @Test
    @Transactional
    public void testCronExpressionWithoutTimeInterval() {

        DateTime dateTime = new DateTime(2016, 2, 3, 13, 55, 0);
        Schedule schedule = new Schedule();
        schedule.setActive(true);
        schedule.setRecurrent(false);
        schedule.setStartTime(dateTime);
        schedule.setVersion(1l);
        schedule.setCreated(dateTime);
        schedule.setLastUpdated(dateTime);
        scheduleService.save(schedule);

        Schedule scheduleSaved = scheduleService.findByStartDate(dateTime);
        String expression = cronExpressionService.buildCronExpression(scheduleSaved);

        log.info("Cron expression without time interval: {}", expression);
    }

    @Test
    @Transactional
    public void testCronWithTimeIntervalNotNull() {

        DateTime dateTime = new DateTime(2016, 2, 3, 13, 55, 0);
        TimeUnit minutesTimeUnit = new TimeUnit();
        minutesTimeUnit.setCode("MIN");
        timeUnitService.save(minutesTimeUnit);
        TimeUnit min = timeUnitService.findByCode("MIN");

        TimeInterval timeInterval = new TimeInterval();
        timeInterval.setName("In fiecare minut");
        timeInterval.setTimeUnit(min);
        timeInterval.setInterval(10l);
        timeIntervalService.save(timeInterval);
        TimeInterval timeIntervalSaved = timeIntervalService.findByName("In fiecare minut");

        Schedule schedule = new Schedule();
        schedule.setStartTime(dateTime);
        schedule.setRecurrent(true);
        schedule.setTimeInterval(timeIntervalSaved);
        scheduleService.save(schedule);

        Schedule scheduleSaved = scheduleService.findByStartDate(dateTime);
        String expression = cronExpressionService.buildCronExpression(scheduleSaved);

        log.info("Cron expression with time interval: {}", expression);
    }

    @Test
    @Transactional
    public void testCronWithTimeIntervalNull() {

        DateTime dateTime = new DateTime(2016, 2, 3, 13, 55, 0);
        Schedule schedule = new Schedule();
        schedule.setActive(true);
        schedule.setRecurrent(true);
        schedule.setStartTime(dateTime);
        schedule.setVersion(1l);
        schedule.setCreated(dateTime);
        schedule.setLastUpdated(dateTime);
        scheduleService.save(schedule);
        Schedule scheduleSaved = scheduleService.findByStartDate(dateTime);

        TimeUnit secondsTimeUnit = new TimeUnit();
        secondsTimeUnit.setCode("SEC");
        timeUnitService.save(secondsTimeUnit);
        TimeUnit minutesTimeUnit = new TimeUnit();
        minutesTimeUnit.setCode("MIN");
        timeUnitService.save(minutesTimeUnit);
        TimeUnit hoursTimeUnit = new TimeUnit();
        hoursTimeUnit.setCode("H");
        timeUnitService.save(hoursTimeUnit);

        TimeUnit sec = timeUnitService.findByCode("SEC");
        TimeUnit min = timeUnitService.findByCode("MIN");

        RecurrentTimeUnit recurrentTimeUnit = new RecurrentTimeUnit();
        recurrentTimeUnit.setValue(12);
        recurrentTimeUnit.setTimeUnit(sec);
        recurrentTimeUnit.setSchedule(scheduleSaved);
        recurrentTimeUnitService.save(recurrentTimeUnit);

        RecurrentTimeUnit recurrentTimeUnit2 = new RecurrentTimeUnit();
        recurrentTimeUnit2.setValue(14);
        recurrentTimeUnit2.setTimeUnit(sec);
        recurrentTimeUnit2.setSchedule(scheduleSaved);
        recurrentTimeUnitService.save(recurrentTimeUnit2);

        RecurrentTimeUnit recurrentTimeUnit1 = new RecurrentTimeUnit();
        recurrentTimeUnit1.setValue(13);
        recurrentTimeUnit1.setTimeUnit(min);
        recurrentTimeUnit1.setSchedule(scheduleSaved);
        recurrentTimeUnitService.save(recurrentTimeUnit1);

        Set<RecurrentTimeUnit> recurrentTimeUnitHashSet = new HashSet<>();
        recurrentTimeUnitHashSet.add(recurrentTimeUnit);
        recurrentTimeUnitHashSet.add(recurrentTimeUnit1);
        recurrentTimeUnitHashSet.add(recurrentTimeUnit2);
        scheduleSaved.setRecurrentTimeUnits(recurrentTimeUnitHashSet);
        scheduleService.save(scheduleSaved);

        Schedule newScheduleWithSet = scheduleService.findByStartDate(dateTime);
        String expression = cronExpressionService.buildCronExpression(newScheduleWithSet);

        log.info("Cron expression with recurrent time unit: {}", expression);
    }
}
