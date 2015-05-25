package ro.teamnet.cron.test;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import ro.teamnet.SchedulerTestApplication;
import ro.teamnet.scheduler.domain.Schedule;
import ro.teamnet.scheduler.domain.TimeInterval;
import ro.teamnet.scheduler.domain.TimeUnit;
import ro.teamnet.scheduler.service.CronExpressionService;
import ro.teamnet.scheduler.service.TimeIntervalService;

import javax.inject.Inject;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SchedulerTestApplication.class)
@WebAppConfiguration
@IntegrationTest
@ActiveProfiles("test-scheduler")
public class CronExpressionTest {

    @Inject
    private CronExpressionService cronExpressionService;

    @Inject
    private TimeIntervalService timeIntervalService;

    @Test
    public void testCronExpression() {

        //TimeInterval timeInterval = timeIntervalService.findById(1l);
        TimeInterval timeInterval = new TimeInterval();
        timeInterval.setName("Lunar");

        Schedule schedule = new Schedule();
        //sec min hours dayOfMonth month dayOfWeek year(optional)
        //schedule.setCron("0 0 12 * * *");
        schedule.setStartTime(new DateTime(2016,2,3,13,55,0));
        schedule.setRecurrent(true);
        schedule.setTimeInterval(timeInterval);
        String expression = cronExpressionService.buildCronExpression(schedule);
        System.out.println(expression);
    }
}
