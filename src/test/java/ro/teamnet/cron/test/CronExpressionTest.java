package ro.teamnet.cron.test;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.junit.Before;
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
import ro.teamnet.scheduler.service.*;

import static junit.framework.Assert.assertEquals;

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

    @Before
    public void preparePersistenceForTestingCronExpression() {

    }

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
        assertEquals("Schedule: start time different values", schedule.getStartTime(), dateTime);

        String expression = cronExpressionService.buildCronExpression(scheduleSaved);
        assertEquals("Cron expression without time interval: different", "0 55 13 3 2 ? 2016", expression);

        log.info("Cron expression without time interval: {}", expression);
    }

    @Test
    @Transactional
    public void testCronWithTimeIntervalNotNull() {

        DateTime dateTime = new DateTime(2016, 2, 3, 13, 55, 0);
        TimeUnit weeksTimeUnit = new TimeUnit();
        weeksTimeUnit.setCode("W");
        timeUnitService.save(weeksTimeUnit);
        TimeUnit week = timeUnitService.findByCode("W");

        TimeInterval weekTimeInterval = new TimeInterval();
        weekTimeInterval.setName("Saptamanal");
        weekTimeInterval.setTimeUnit(week);
        weekTimeInterval.setInterval(7l);
        timeIntervalService.save(weekTimeInterval);
        TimeInterval weekTimeIntervalSaved = timeIntervalService.findByName("Saptamanal");

        Schedule schedule = new Schedule();
        schedule.setStartTime(dateTime);
        schedule.setRecurrent(true);
        schedule.setTimeInterval(weekTimeIntervalSaved);
        scheduleService.save(schedule);

        Schedule scheduleSaved = scheduleService.findByStartDate(dateTime);
        assertEquals("Schedule: start time different values", schedule.getStartTime(), dateTime);

        String expression = cronExpressionService.buildCronExpression(scheduleSaved);
        assertEquals("Cron expression with time interval: different", "0 55 13 3 2 4/7 2016", expression);

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
        assertEquals("Schedule: start time different values", schedule.getStartTime(), dateTime);

        TimeUnit secondsTimeUnit = new TimeUnit();
        secondsTimeUnit.setCode("SEC");
        timeUnitService.save(secondsTimeUnit);
        TimeUnit minutesTimeUnit = new TimeUnit();
        minutesTimeUnit.setCode("MIN");
        timeUnitService.save(minutesTimeUnit);
        TimeUnit hoursTimeUnit = new TimeUnit();
        hoursTimeUnit.setCode("H");
        timeUnitService.save(hoursTimeUnit);
        TimeUnit daysTimeUnit = new TimeUnit();
        daysTimeUnit.setCode("D");
        timeUnitService.save(daysTimeUnit);
        TimeUnit monthsTimeUnit = new TimeUnit();
        monthsTimeUnit.setCode("MON");
        timeUnitService.save(monthsTimeUnit);
        TimeUnit weeksTimeUnit = new TimeUnit();
        weeksTimeUnit.setCode("W");
        timeUnitService.save(weeksTimeUnit);
        TimeUnit yearsTimeUnit = new TimeUnit();
        yearsTimeUnit.setCode("Y");
        timeUnitService.save(yearsTimeUnit);

        TimeUnit sec = timeUnitService.findByCode("SEC");
        log.info("TimeUnit seconds: {}", sec.getCode());
        assertEquals("TimeUnit seconds: different", sec.getCode(), "SEC");
        TimeUnit min = timeUnitService.findByCode("MIN");
        log.info("TimeUnit seconds: {}", min.getCode());
        assertEquals("TimeUnit seconds: different", min.getCode(), "MIN");
        TimeUnit hour = timeUnitService.findByCode("H");
        log.info("TimeUnit seconds: {}", hour.getCode());
        assertEquals("TimeUnit seconds: different", hour.getCode(), "H");
        TimeUnit day = timeUnitService.findByCode("D");
        log.info("TimeUnit seconds: {}", day.getCode());
        assertEquals("TimeUnit seconds: different", day.getCode(), "D");
        TimeUnit month = timeUnitService.findByCode("MON");
        log.info("TimeUnit seconds: {}", month.getCode());
        assertEquals("TimeUnit seconds: different", month.getCode(), "MON");
        TimeUnit week = timeUnitService.findByCode("W");
        log.info("TimeUnit seconds: {}", week.getCode());
        assertEquals("TimeUnit seconds: different", week.getCode(), "W");
        TimeUnit year = timeUnitService.findByCode("Y");
        log.info("TimeUnit seconds: {}", year.getCode());
        assertEquals("TimeUnit seconds: different", year.getCode(), "Y");

        RecurrentTimeUnit recurrentTimeUnit = new RecurrentTimeUnit();
        recurrentTimeUnit.setValue(12);
        recurrentTimeUnit.setTimeUnit(sec);
        recurrentTimeUnit.setSchedule(scheduleSaved);
        recurrentTimeUnitService.save(recurrentTimeUnit);
        RecurrentTimeUnit recurrentTimeUnitSaved = recurrentTimeUnitService.findByTimeUnitAndValueAndSchedule(sec, 12, scheduleSaved);
        assertEquals("RecurrentTimeUnit seconds: different value", recurrentTimeUnitSaved.getValue().intValue(), 12);

        RecurrentTimeUnit recurrentTimeUnit2 = new RecurrentTimeUnit();
        recurrentTimeUnit2.setValue(14);
        recurrentTimeUnit2.setTimeUnit(sec);
        recurrentTimeUnit2.setSchedule(scheduleSaved);
        recurrentTimeUnitService.save(recurrentTimeUnit2);
        RecurrentTimeUnit recurrentTimeUnit2Saved = recurrentTimeUnitService.findByTimeUnitAndValueAndSchedule(sec, 14, scheduleSaved);
        assertEquals("RecurrentTimeUnit seconds: different value", recurrentTimeUnit2Saved.getValue().intValue(), 14);

        RecurrentTimeUnit recurrentTimeUnit1 = new RecurrentTimeUnit();
        recurrentTimeUnit1.setValue(13);
        recurrentTimeUnit1.setTimeUnit(min);
        recurrentTimeUnit1.setSchedule(scheduleSaved);
        recurrentTimeUnitService.save(recurrentTimeUnit1);
        RecurrentTimeUnit recurrentTimeUnit1Saved = recurrentTimeUnitService.findByTimeUnitAndValueAndSchedule(min, 13, scheduleSaved);
        assertEquals("RecurrentTimeUnit minutes: different value", recurrentTimeUnit1Saved.getValue().intValue(), 13);

        RecurrentTimeUnit recurrentTimeUnit3 = new RecurrentTimeUnit();
        recurrentTimeUnit3.setValue(2);
        recurrentTimeUnit3.setTimeUnit(hour);
        recurrentTimeUnit3.setSchedule(scheduleSaved);
        recurrentTimeUnitService.save(recurrentTimeUnit3);
        RecurrentTimeUnit recurrentTimeUnit3Saved = recurrentTimeUnitService.findByTimeUnitAndValueAndSchedule(hour, 2, scheduleSaved);
        assertEquals("RecurrentTimeUnit hours: different value", recurrentTimeUnit3Saved.getValue().intValue(), 2);

        RecurrentTimeUnit recurrentTimeUnit4 = new RecurrentTimeUnit();
        recurrentTimeUnit4.setValue(2);
        recurrentTimeUnit4.setTimeUnit(month);
        recurrentTimeUnit4.setSchedule(scheduleSaved);
        recurrentTimeUnitService.save(recurrentTimeUnit4);
        RecurrentTimeUnit recurrentTimeUnit4Saved = recurrentTimeUnitService.findByTimeUnitAndValueAndSchedule(month, 2, scheduleSaved);
        log.info("RecurrentTimeUnit month: {}", recurrentTimeUnit4Saved.getValue());
        assertEquals("RecurrentTimeUnit month: different value", recurrentTimeUnit4Saved.getValue().intValue(), 2);
        assertEquals("RecurrentTimeUnit time unit: different value", recurrentTimeUnit4Saved.getTimeUnit().getCode(), "MON");

        RecurrentTimeUnit recurrentTimeUnit5 = new RecurrentTimeUnit();
        recurrentTimeUnit5.setValue(3);
        recurrentTimeUnit5.setTimeUnit(week);
        recurrentTimeUnit5.setSchedule(scheduleSaved);
        recurrentTimeUnitService.save(recurrentTimeUnit5);
        RecurrentTimeUnit recurrentTimeUnit5Saved = recurrentTimeUnitService.findByTimeUnitAndValueAndSchedule(week, 3, scheduleSaved);
        log.info("RecurrentTimeUnit week: {}", recurrentTimeUnit5Saved.getValue());
        assertEquals("RecurrentTimeUnit week: different value", recurrentTimeUnit5Saved.getValue().intValue(), 3);
        assertEquals("RecurrentTimeUnit time unit: different value", recurrentTimeUnit5Saved.getTimeUnit().getCode(), "W");

        Set<RecurrentTimeUnit> recurrentTimeUnitHashSet = new HashSet<>();
        recurrentTimeUnitHashSet.add(recurrentTimeUnitSaved);
        recurrentTimeUnitHashSet.add(recurrentTimeUnit1Saved);
        recurrentTimeUnitHashSet.add(recurrentTimeUnit2Saved);
        recurrentTimeUnitHashSet.add(recurrentTimeUnit3Saved);
        recurrentTimeUnitHashSet.add(recurrentTimeUnit4Saved);
        recurrentTimeUnitHashSet.add(recurrentTimeUnit5Saved);
        scheduleSaved.setRecurrentTimeUnits(recurrentTimeUnitHashSet);
        scheduleService.save(scheduleSaved);

        Schedule newScheduleWithSet = scheduleService.findByStartDate(dateTime);
        assertEquals("Schedule after update start date: different value", newScheduleWithSet.getStartTime(), dateTime);

        String expression = cronExpressionService.buildCronExpression(newScheduleWithSet);
        assertEquals("Cron expression with time interval: different", "12,14 13 2 * 2 4 *", expression);

        log.info("Cron expression with recurrent time unit: {}", expression);
    }

    @Test
    public void testGetCronWeekDayCode() {

        int sundayValueReturned = cronExpressionService.getCronWeekDayCode(DateTimeConstants.SUNDAY);
        assertEquals("Sunday: {}", sundayValueReturned, 1);
        int mondayValueReturned = cronExpressionService.getCronWeekDayCode(DateTimeConstants.MONDAY);
        assertEquals("Monday: {}", mondayValueReturned, 2);
        int tuesdayValueReturned = cronExpressionService.getCronWeekDayCode(DateTimeConstants.TUESDAY);
        assertEquals("Tuesday: {}", tuesdayValueReturned, 3);
        int wednesdayValueReturned = cronExpressionService.getCronWeekDayCode(DateTimeConstants.WEDNESDAY);
        assertEquals("Wednesday: {}", wednesdayValueReturned, 4);
        int thursdayValueReturned = cronExpressionService.getCronWeekDayCode(DateTimeConstants.THURSDAY);
        assertEquals("Thursday: {}", thursdayValueReturned, 5);
        int fridayValueReturned = cronExpressionService.getCronWeekDayCode(DateTimeConstants.FRIDAY);
        assertEquals("Friday: {}", fridayValueReturned, 6);
        int saturdayValueReturned = cronExpressionService.getCronWeekDayCode(DateTimeConstants.SATURDAY);
        assertEquals("Saturday: {}", saturdayValueReturned, 7);
    }

    @Test
    @Transactional
    public void testDayOfWeekVsDayOfMonth() {

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
        assertEquals("Schedule: start time different values", schedule.getStartTime(), dateTime);

        TimeUnit daysTimeUnit = new TimeUnit();
        daysTimeUnit.setCode("D");
        timeUnitService.save(daysTimeUnit);
        TimeUnit weeksTimeUnit = new TimeUnit();
        weeksTimeUnit.setCode("W");
        timeUnitService.save(weeksTimeUnit);

        TimeUnit day = timeUnitService.findByCode("D");
        log.info("TimeUnit days: {}", day.getCode());
        assertEquals("TimeUnit days: different", day.getCode(), "D");

        RecurrentTimeUnit recurrentTimeUnit = new RecurrentTimeUnit();
        recurrentTimeUnit.setValue(12);
        recurrentTimeUnit.setTimeUnit(day);
        recurrentTimeUnit.setSchedule(scheduleSaved);
        recurrentTimeUnitService.save(recurrentTimeUnit);
        RecurrentTimeUnit recurrentTimeUnitSaved = recurrentTimeUnitService.findByTimeUnitAndValueAndSchedule(day, 12, scheduleSaved);
        assertEquals("RecurrentTimeUnit day: different value", recurrentTimeUnitSaved.getValue().intValue(), 12);

        Set<RecurrentTimeUnit> recurrentTimeUnitHashSet = new HashSet<>();
        recurrentTimeUnitHashSet.add(recurrentTimeUnitSaved);
        scheduleSaved.setRecurrentTimeUnits(recurrentTimeUnitHashSet);
        scheduleService.save(scheduleSaved);

        Schedule newScheduleWithSet = scheduleService.findByStartDate(dateTime);
        assertEquals("Schedule after update start date: different value", newScheduleWithSet.getStartTime(), dateTime);

        String expression = cronExpressionService.buildCronExpression(newScheduleWithSet);
        assertEquals("Cron expression with time interval: different", "0 * * 12 * * *", expression);

        log.info("Cron expression with recurrent time unit: {}", expression);
    }

    @Test
    @Transactional
    public void testDayOfMonthVsDayOfWeek() {
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
        assertEquals("Schedule: start time different values", schedule.getStartTime(), dateTime);

        TimeUnit weeksTimeUnit = new TimeUnit();
        weeksTimeUnit.setCode("W");
        timeUnitService.save(weeksTimeUnit);

        TimeUnit week = timeUnitService.findByCode("W");
        assertEquals("TimeUnit week: different", week.getCode(), "W");

        RecurrentTimeUnit recurrentTimeUnit = new RecurrentTimeUnit();
        recurrentTimeUnit.setValue(6);
        recurrentTimeUnit.setTimeUnit(week);
        recurrentTimeUnit.setSchedule(scheduleSaved);
        recurrentTimeUnitService.save(recurrentTimeUnit);
        RecurrentTimeUnit recurrentTimeUnitSaved = recurrentTimeUnitService.findByTimeUnitAndValueAndSchedule(week, 6, scheduleSaved);
        assertEquals("RecurrentTimeUnit week: different value", recurrentTimeUnitSaved.getValue().intValue(), 6);

        Set<RecurrentTimeUnit> recurrentTimeUnitHashSet = new HashSet<>();
        recurrentTimeUnitHashSet.add(recurrentTimeUnitSaved);
        scheduleSaved.setRecurrentTimeUnits(recurrentTimeUnitHashSet);
        scheduleService.save(scheduleSaved);

        Schedule newScheduleWithSet = scheduleService.findByStartDate(dateTime);
        assertEquals("Schedule after update start date: different value", newScheduleWithSet.getStartTime(), dateTime);

        String expression = cronExpressionService.buildCronExpression(newScheduleWithSet);
        assertEquals("Cron expression with time interval: different", "0 * * * * 7 *", expression);

        log.info("Cron expression with recurrent time unit: {}", expression);
    }

    @Test
    @Transactional
    public void testDayOfMonthVsDayOfWeekBothTheSame() {
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
        assertEquals("Schedule: start time different values", schedule.getStartTime(), dateTime);

        TimeUnit daysTimeUnit = new TimeUnit();
        daysTimeUnit.setCode("MIN");
        timeUnitService.save(daysTimeUnit);
        TimeUnit weeksTimeUnit = new TimeUnit();
        weeksTimeUnit.setCode("H");
        timeUnitService.save(weeksTimeUnit);

        TimeUnit min = timeUnitService.findByCode("MIN");
        assertEquals("TimeUnit minutes: different", min.getCode(), "MIN");
        TimeUnit hour = timeUnitService.findByCode("H");
        assertEquals("TimeUnit hours: different", hour.getCode(), "H");

        RecurrentTimeUnit recurrentTimeUnit = new RecurrentTimeUnit();
        recurrentTimeUnit.setValue(15);
        recurrentTimeUnit.setTimeUnit(min);
        recurrentTimeUnit.setSchedule(scheduleSaved);
        recurrentTimeUnitService.save(recurrentTimeUnit);
        RecurrentTimeUnit recurrentTimeUnitSaved = recurrentTimeUnitService.findByTimeUnitAndValueAndSchedule(min, 15, scheduleSaved);
        assertEquals("RecurrentTimeUnit minutes: different value", recurrentTimeUnitSaved.getValue().intValue(), 15);
        RecurrentTimeUnit recurrentTimeUnit1 = new RecurrentTimeUnit();
        recurrentTimeUnit1.setValue(5);
        recurrentTimeUnit1.setTimeUnit(hour);
        recurrentTimeUnit1.setSchedule(scheduleSaved);
        recurrentTimeUnitService.save(recurrentTimeUnit1);
        RecurrentTimeUnit recurrentTimeUnit1Saved = recurrentTimeUnitService.findByTimeUnitAndValueAndSchedule(hour, 5, scheduleSaved);
        assertEquals("RecurrentTimeUnit hours: different value", recurrentTimeUnit1Saved.getValue().intValue(), 5);

        Set<RecurrentTimeUnit> recurrentTimeUnitHashSet = new HashSet<>();
        recurrentTimeUnitHashSet.add(recurrentTimeUnitSaved);
        recurrentTimeUnitHashSet.add(recurrentTimeUnit1Saved);
        scheduleSaved.setRecurrentTimeUnits(recurrentTimeUnitHashSet);
        scheduleService.save(scheduleSaved);

        Schedule newScheduleWithSet = scheduleService.findByStartDate(dateTime);
        assertEquals("Schedule after update start date: different value", newScheduleWithSet.getStartTime(), dateTime);

        String expression = cronExpressionService.buildCronExpression(newScheduleWithSet);
        assertEquals("Cron expression with time interval: different", "0 15 5 * * ? *", expression);

        log.info("Cron expression with recurrent time unit: {}", expression);
    }

    @Test
    @Transactional
    public void testValueDayOfMonthVsDayOfWeek() {
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
        assertEquals("Schedule: start time different values", schedule.getStartTime(), dateTime);

        TimeUnit minTimeUnit = new TimeUnit();
        minTimeUnit.setCode("MIN");
        timeUnitService.save(minTimeUnit);
        TimeUnit weeksTimeUnit = new TimeUnit();
        weeksTimeUnit.setCode("H");
        timeUnitService.save(weeksTimeUnit);
        TimeUnit daysTimeUnit = new TimeUnit();
        daysTimeUnit.setCode("D");
        timeUnitService.save(daysTimeUnit);

        TimeUnit min = timeUnitService.findByCode("MIN");
        assertEquals("TimeUnit minutes: different", min.getCode(), "MIN");
        TimeUnit hour = timeUnitService.findByCode("H");
        assertEquals("TimeUnit hours: different", hour.getCode(), "H");
        TimeUnit day = timeUnitService.findByCode("D");
        assertEquals("TimeUnit hours: different", day.getCode(), "D");

        RecurrentTimeUnit recurrentTimeUnit = new RecurrentTimeUnit();
        recurrentTimeUnit.setValue(15);
        recurrentTimeUnit.setTimeUnit(min);
        recurrentTimeUnit.setSchedule(scheduleSaved);
        recurrentTimeUnitService.save(recurrentTimeUnit);
        RecurrentTimeUnit recurrentTimeUnitSaved = recurrentTimeUnitService.findByTimeUnitAndValueAndSchedule(min, 15, scheduleSaved);
        assertEquals("RecurrentTimeUnit minutes: different value", recurrentTimeUnitSaved.getValue().intValue(), 15);
        RecurrentTimeUnit recurrentTimeUnit1 = new RecurrentTimeUnit();
        recurrentTimeUnit1.setValue(5);
        recurrentTimeUnit1.setTimeUnit(hour);
        recurrentTimeUnit1.setSchedule(scheduleSaved);
        recurrentTimeUnitService.save(recurrentTimeUnit1);
        RecurrentTimeUnit recurrentTimeUnit1Saved = recurrentTimeUnitService.findByTimeUnitAndValueAndSchedule(hour, 5, scheduleSaved);
        assertEquals("RecurrentTimeUnit hours: different value", recurrentTimeUnit1Saved.getValue().intValue(), 5);
        RecurrentTimeUnit recurrentTimeUnit2 = new RecurrentTimeUnit();
        recurrentTimeUnit2.setValue(21);
        recurrentTimeUnit2.setTimeUnit(day);
        recurrentTimeUnit2.setSchedule(scheduleSaved);
        recurrentTimeUnitService.save(recurrentTimeUnit2);
        RecurrentTimeUnit recurrentTimeUnit2Saved = recurrentTimeUnitService.findByTimeUnitAndValueAndSchedule(day, 21, scheduleSaved);
        assertEquals("RecurrentTimeUnit days: different value", recurrentTimeUnit2Saved.getValue().intValue(), 21);

        Set<RecurrentTimeUnit> recurrentTimeUnitHashSet = new HashSet<>();
        recurrentTimeUnitHashSet.add(recurrentTimeUnitSaved);
        recurrentTimeUnitHashSet.add(recurrentTimeUnit1Saved);
        recurrentTimeUnitHashSet.add(recurrentTimeUnit2Saved);
        scheduleSaved.setRecurrentTimeUnits(recurrentTimeUnitHashSet);
        scheduleService.save(scheduleSaved);

        Schedule newScheduleWithSet = scheduleService.findByStartDate(dateTime);
        assertEquals("Schedule after update start date: different value", newScheduleWithSet.getStartTime(), dateTime);

        String expression = cronExpressionService.buildCronExpression(newScheduleWithSet);
        assertEquals("Cron expression with time interval: different", "0 15 5 21 * * *", expression);

        log.info("Cron expression with recurrent time unit: {}", expression);
    }
}
