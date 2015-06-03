package ro.teamnet.cron.test;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
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

import javax.inject.Inject;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SchedulerTestApplication.class)
@WebAppConfiguration
@IntegrationTest
@ActiveProfiles("test-scheduler")
@Transactional
public class CronExpressionTest {

    @Inject
    private CronExpressionService cronExpressionService;

    private Logger log = LoggerFactory.getLogger(CronExpressionTest.class);
    public static final DateTime DATE_TIME = new DateTime(2016, 2, 3, 13, 55, 0);

    private Schedule createSchedule(boolean recurrent) {
        Schedule schedule = new Schedule();
        schedule.setActive(true);
        schedule.setRecurrent(recurrent);
        schedule.setStartTime(DATE_TIME);

        return schedule;
    }

    private TimeUnit createTimeUnit(String code) {
        TimeUnit timeUnit = new TimeUnit();
        timeUnit.setCode(code);

        return timeUnit;
    }

    private TimeInterval createTimeInterval(String name, TimeUnit timeUnit, Long interval) {
        TimeInterval weekTimeInterval = new TimeInterval();
        weekTimeInterval.setName(name);
        weekTimeInterval.setTimeUnit(timeUnit);
        weekTimeInterval.setInterval(interval);

        return weekTimeInterval;
    }

    private RecurrentTimeUnit createRecurrentTimeUnit(Long id, Integer value, TimeUnit timeUnit, Schedule schedule) {
        RecurrentTimeUnit recurrentTimeUnit = new RecurrentTimeUnit();
        recurrentTimeUnit.setId(id);
        recurrentTimeUnit.setValue(value);
        recurrentTimeUnit.setTimeUnit(timeUnit);
        recurrentTimeUnit.setSchedule(schedule);

        return recurrentTimeUnit;
    }

    @Test
    public void testCronExpressionWithoutTimeInterval() {

        String expression = cronExpressionService.buildCronExpressionForRecurrentFalse(createSchedule(false));
        assertEquals("Cron expression without time interval: different", "0 55 13 3 2 ? 2016", expression);

        log.info("Cron expression without time interval: {}", expression);
    }

    @Test
    public void testCronWithTimeIntervalNotNull() {

        Schedule schedule = createSchedule(true);
        schedule.setTimeInterval(createTimeInterval("Saptamanal", createTimeUnit("W"), 1l));

        String expression = cronExpressionService.buildCronExpressionForRecurrentTrueWithTimeInterval(schedule);
        assertEquals("Cron expression with time interval: different", "0 55 13 3/7 2 ? 2016", expression);

        log.info("Cron expression with time interval: {}", expression);
    }

    @Test
    public void testCronWithTimeIntervalNull() {

        Schedule schedule = createSchedule(true);

        RecurrentTimeUnit recurrentTimeUnit = createRecurrentTimeUnit(1l, 12, createTimeUnit("SEC"), schedule);
        RecurrentTimeUnit recurrentTimeUnit1 = createRecurrentTimeUnit(2l, 14, createTimeUnit("SEC"), schedule);
        RecurrentTimeUnit recurrentTimeUnit2 = createRecurrentTimeUnit(3l, 13, createTimeUnit("D"), schedule);
        RecurrentTimeUnit recurrentTimeUnit3 = createRecurrentTimeUnit(4l, 2, createTimeUnit("H"), schedule);
        RecurrentTimeUnit recurrentTimeUnit4 = createRecurrentTimeUnit(5l, 2, createTimeUnit("MON"), schedule);
        RecurrentTimeUnit recurrentTimeUnit5 = createRecurrentTimeUnit(6l, 3, createTimeUnit("W"), schedule);

        Set<RecurrentTimeUnit> recurrentTimeUnitHashSet = new TreeSet<>(defaultComparator);
        recurrentTimeUnitHashSet.add(recurrentTimeUnit);
        recurrentTimeUnitHashSet.add(recurrentTimeUnit1);
        recurrentTimeUnitHashSet.add(recurrentTimeUnit2);
        recurrentTimeUnitHashSet.add(recurrentTimeUnit3);
        recurrentTimeUnitHashSet.add(recurrentTimeUnit4);
        recurrentTimeUnitHashSet.add(recurrentTimeUnit5);
        schedule.setRecurrentTimeUnits(recurrentTimeUnitHashSet);

        String expression = cronExpressionService.buildCronExpressionForRecurrentTrueWithRecurrentTimeUnit(schedule);
        assertEquals("Cron expression with time interval: different", "12,14 55 2 13 2 ? *", expression);

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
    public void testDayOfWeekVsDayOfMonth() {

        Schedule schedule = createSchedule(true);

        RecurrentTimeUnit recurrentTimeUnit = createRecurrentTimeUnit(1l, 12, createTimeUnit("D"), schedule);

        Set<RecurrentTimeUnit> recurrentTimeUnitHashSet = new TreeSet<>(defaultComparator);
        recurrentTimeUnitHashSet.add(recurrentTimeUnit);
        schedule.setRecurrentTimeUnits(recurrentTimeUnitHashSet);

        String expression = cronExpressionService.buildCronExpressionForRecurrentTrueWithRecurrentTimeUnit(schedule);
        assertEquals("Cron expression with time interval: different", "0 55 * 12 * ? *", expression);

        log.info("Cron expression with recurrent time unit: {}", expression);
    }

    @Test
    public void testDayOfMonthVsDayOfWeek() {

        Schedule schedule = createSchedule(true);

        RecurrentTimeUnit recurrentTimeUnit = createRecurrentTimeUnit(1l, 6, createTimeUnit("W"), schedule);

        Set<RecurrentTimeUnit> recurrentTimeUnitHashSet = new TreeSet<>(defaultComparator);
        recurrentTimeUnitHashSet.add(recurrentTimeUnit);
        schedule.setRecurrentTimeUnits(recurrentTimeUnitHashSet);

        String expression = cronExpressionService.buildCronExpressionForRecurrentTrueWithRecurrentTimeUnit(schedule);
        assertEquals("Cron expression with time interval: different", "0 55 * ? * 7 *", expression);

        log.info("Cron expression with recurrent time unit: {}", expression);
    }

    @Test
    public void testDayOfMonthVsDayOfWeekBothTheSame() {

        Schedule schedule = createSchedule(true);

        RecurrentTimeUnit recurrentTimeUnit = createRecurrentTimeUnit(1l, 15, createTimeUnit("D"), schedule);
        RecurrentTimeUnit recurrentTimeUnit1 = createRecurrentTimeUnit(2l, 5, createTimeUnit("H"), schedule);

        Set<RecurrentTimeUnit> recurrentTimeUnitHashSet = new TreeSet<>(defaultComparator);
        recurrentTimeUnitHashSet.add(recurrentTimeUnit);
        recurrentTimeUnitHashSet.add(recurrentTimeUnit1);
        schedule.setRecurrentTimeUnits(recurrentTimeUnitHashSet);

        String expression = cronExpressionService.buildCronExpressionForRecurrentTrueWithRecurrentTimeUnit(schedule);
        assertEquals("Cron expression with time interval: different", "0 55 5 15 * ? *", expression);

        log.info("Cron expression with recurrent time unit: {}", expression);
    }

    @Test
    public void testValueDayOfMonthVsDayOfWeek() {

        Schedule schedule = createSchedule(true);

        RecurrentTimeUnit recurrentTimeUnit = createRecurrentTimeUnit(1l, 10, createTimeUnit("MON"), schedule);
        RecurrentTimeUnit recurrentTimeUnit1 = createRecurrentTimeUnit(2l, 5, createTimeUnit("H"), schedule);
        RecurrentTimeUnit recurrentTimeUnit2 = createRecurrentTimeUnit(3l, 21, createTimeUnit("D"), schedule);

        Set<RecurrentTimeUnit> recurrentTimeUnitHashSet = new TreeSet<>(defaultComparator);
        recurrentTimeUnitHashSet.add(recurrentTimeUnit);
        recurrentTimeUnitHashSet.add(recurrentTimeUnit1);
        recurrentTimeUnitHashSet.add(recurrentTimeUnit2);
        schedule.setRecurrentTimeUnits(recurrentTimeUnitHashSet);

        String expression = cronExpressionService.buildCronExpressionForRecurrentTrueWithRecurrentTimeUnit(schedule);
        assertEquals("Cron expression with time interval: different", "0 55 5 21 10 ? *", expression);

        log.info("Cron expression with recurrent time unit: {}", expression);
    }

    @Test
    public void testCronExpressionWithAllDays() {

        Schedule schedule = createSchedule(true);

        RecurrentTimeUnit recurrentTimeUnit = createRecurrentTimeUnit(1l, -1, createTimeUnit("D"), schedule);

        Set<RecurrentTimeUnit> recurrentTimeUnitHashSet = new TreeSet<>(defaultComparator);
        recurrentTimeUnitHashSet.add(recurrentTimeUnit);
        schedule.setRecurrentTimeUnits(recurrentTimeUnitHashSet);

        String expression = cronExpressionService.buildCronExpressionForRecurrentTrueWithRecurrentTimeUnit(schedule);
        assertEquals("Cron expression with time interval: different", "0 55 * * * ? *", expression);

        log.info("Cron expression with recurrent time unit: {}", expression);
    }

    @Test
    public void testCronExpressionWithAllDaysAndMonths() {

        Schedule schedule = createSchedule(true);

        RecurrentTimeUnit recurrentTimeUnitHour = createRecurrentTimeUnit(1l, 18, createTimeUnit("H"), schedule);
        RecurrentTimeUnit recurrentTimeUnit = createRecurrentTimeUnit(2l, -1, createTimeUnit("D"), schedule);
        RecurrentTimeUnit recurrentTimeUnitMonth = createRecurrentTimeUnit(3l, -1, createTimeUnit("MON"), schedule);

        Set<RecurrentTimeUnit> recurrentTimeUnitHashSet = new TreeSet<>(defaultComparator);
        recurrentTimeUnitHashSet.add(recurrentTimeUnit);
        recurrentTimeUnitHashSet.add(recurrentTimeUnitMonth);
        recurrentTimeUnitHashSet.add(recurrentTimeUnitHour);
        schedule.setRecurrentTimeUnits(recurrentTimeUnitHashSet);

        String expression = cronExpressionService.buildCronExpressionForRecurrentTrueWithRecurrentTimeUnit(schedule);
        assertEquals("Cron expression with time interval: different", "0 55 18 * * ? *", expression);

        log.info("Cron expression with recurrent time unit: {}", expression);
    }

    @Test
    public void testCronExpressionWithManyData() {
        Schedule schedule = createSchedule(true);

        RecurrentTimeUnit recurrentTimeUnitSec = createRecurrentTimeUnit(1l, 15, createTimeUnit("SEC"), schedule);
        RecurrentTimeUnit recurrentTimeUnitSec1 = createRecurrentTimeUnit(2l, 30, createTimeUnit("SEC"), schedule);
        RecurrentTimeUnit recurrentTimeUnitSec2 = createRecurrentTimeUnit(3l, 55, createTimeUnit("SEC"), schedule);

        RecurrentTimeUnit recurrentTimeUnitHour = createRecurrentTimeUnit(7l, 15, createTimeUnit("H"), schedule);
        RecurrentTimeUnit recurrentTimeUnitHour1 = createRecurrentTimeUnit(8l, 30, createTimeUnit("H"), schedule);
        RecurrentTimeUnit recurrentTimeUnitHour2 = createRecurrentTimeUnit(9l, 55, createTimeUnit("H"), schedule);

        RecurrentTimeUnit recurrentTimeUnitDay = createRecurrentTimeUnit(10l, 15, createTimeUnit("D"), schedule);
        RecurrentTimeUnit recurrentTimeUnitDay1 = createRecurrentTimeUnit(11l, 30, createTimeUnit("D"), schedule);
        RecurrentTimeUnit recurrentTimeUnitDay2 = createRecurrentTimeUnit(12l, 55, createTimeUnit("D"), schedule);

        RecurrentTimeUnit recurrentTimeUnitMonth = createRecurrentTimeUnit(13l, 2, createTimeUnit("MON"), schedule);
        RecurrentTimeUnit recurrentTimeUnitMonth1 = createRecurrentTimeUnit(14l, 4, createTimeUnit("MON"), schedule);
        RecurrentTimeUnit recurrentTimeUnitMonth2 = createRecurrentTimeUnit(15l, 6, createTimeUnit("MON"), schedule);

        RecurrentTimeUnit recurrentTimeUnitWeek = createRecurrentTimeUnit(16l, 2, createTimeUnit("W"), schedule);
        RecurrentTimeUnit recurrentTimeUnitWeek1 = createRecurrentTimeUnit(17l, 4, createTimeUnit("W"), schedule);
        RecurrentTimeUnit recurrentTimeUnitWeek2 = createRecurrentTimeUnit(18l, 6, createTimeUnit("W"), schedule);

        Set<RecurrentTimeUnit> recurrentTimeUnitHashSet = new TreeSet<>(defaultComparator);
        recurrentTimeUnitHashSet.add(recurrentTimeUnitSec);
        recurrentTimeUnitHashSet.add(recurrentTimeUnitSec1);
        recurrentTimeUnitHashSet.add(recurrentTimeUnitSec2);
        recurrentTimeUnitHashSet.add(recurrentTimeUnitHour);
        recurrentTimeUnitHashSet.add(recurrentTimeUnitHour1);
        recurrentTimeUnitHashSet.add(recurrentTimeUnitHour2);
        recurrentTimeUnitHashSet.add(recurrentTimeUnitDay);
        recurrentTimeUnitHashSet.add(recurrentTimeUnitDay1);
        recurrentTimeUnitHashSet.add(recurrentTimeUnitDay2);
        recurrentTimeUnitHashSet.add(recurrentTimeUnitMonth);
        recurrentTimeUnitHashSet.add(recurrentTimeUnitMonth1);
        recurrentTimeUnitHashSet.add(recurrentTimeUnitMonth2);
        recurrentTimeUnitHashSet.add(recurrentTimeUnitWeek);
        recurrentTimeUnitHashSet.add(recurrentTimeUnitWeek1);
        recurrentTimeUnitHashSet.add(recurrentTimeUnitWeek2);
        schedule.setRecurrentTimeUnits(recurrentTimeUnitHashSet);

        String expression = cronExpressionService.buildCronExpressionForRecurrentTrueWithRecurrentTimeUnit(schedule);
        assertEquals("Cron expression with time interval: different", "15,30,55 55 15,30,55 15,30,55 2,4,6 ? *", expression);

        log.info("Cron expression with recurrent time unit: {}", expression);
    }

    private Comparator<Object> defaultComparator = new Comparator<Object>() {
        @Override
        public int compare(Object o1, Object o2) {
            RecurrentTimeUnit obj1 = (RecurrentTimeUnit)o1;
            RecurrentTimeUnit obj2 = (RecurrentTimeUnit)o2;
            return obj1.getId().compareTo(obj2.getId());
        }
    };
}
