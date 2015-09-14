package ro.teamnet.scheduler.service;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ro.teamnet.SchedulerTestApplication;
import ro.teamnet.scheduler.domain.Schedule;
import ro.teamnet.scheduler.domain.ScheduledJob;
import ro.teamnet.scheduler.domain.TimeInterval;
import ro.teamnet.scheduler.domain.TimeUnit;
import ro.teamnet.scheduler.dto.ConfigurationDTO;
import ro.teamnet.scheduler.dto.SchedulingBaseDTO;
import ro.teamnet.scheduler.dto.TimeIntervalDTO;
import ro.teamnet.scheduler.dto.schedule.BasicRecurrentScheduleDTO;
import ro.teamnet.scheduler.dto.schedule.SingleExecutionScheduleDTO;
import ro.teamnet.scheduler.enums.TimeUnitCode;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by Oana.Mihai on 9/14/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SchedulerTestApplication.class)
@WebAppConfiguration
@IntegrationTest
@ActiveProfiles("test-scheduler")
@Transactional
public class PublicSchedulingServiceTest {
    @Inject
    private SchedulingService publicSchedulingService;
    @Inject
    private ScheduledJobService scheduledJobService;
    @Inject
    private ScheduleService scheduleService;
    @Inject
    private TimeUnitService timeUnitService;
    @Inject
    private TimeIntervalService timeIntervalService;

    @Test
    public void testPublicScheduler() {
        ConfigurationDTO configurationDTO = new ConfigurationDTO(1L, "TEST_CFG");
        createTimeUnitWithInterval(TimeUnitCode.D);
        createTimeUnitWithInterval(TimeUnitCode.H);
        publicSchedulingService.createJob(configurationDTO);
        SchedulingBaseDTO schedulingBase = publicSchedulingService.getSchedulingBase(configurationDTO);

        Long firstSchedule = publicSchedulingService.scheduleJob(configurationDTO, new SingleExecutionScheduleDTO(new DateTime()));
        Long secondSchedule = publicSchedulingService.scheduleJob(configurationDTO, new BasicRecurrentScheduleDTO(new DateTime(),
                new TimeIntervalDTO(1l, TimeUnitCode.D)));
        Long thirdSchedule = publicSchedulingService.scheduleJob(configurationDTO, new BasicRecurrentScheduleDTO(new DateTime(),
                new TimeIntervalDTO(12l, TimeUnitCode.H)));

        ScheduledJob scheduledJob = scheduledJobService.findOne(schedulingBase.getId());
        Assert.assertNotNull(scheduledJob);
        List<Schedule> schedules = scheduleService.findByScheduledJobId(scheduledJob.getId());
        Assert.assertEquals(3, schedules.size());
        Assert.assertFalse(scheduleService.findOne(firstSchedule).getDeleted());
        publicSchedulingService.unscheduleJob(configurationDTO, firstSchedule);
        Assert.assertTrue(scheduleService.findOne(firstSchedule).getDeleted());
        Assert.assertFalse(scheduleService.findOne(secondSchedule).getDeleted());
        Assert.assertFalse(scheduleService.findOne(thirdSchedule).getDeleted());
        publicSchedulingService.unscheduleJob(configurationDTO);
        Assert.assertTrue(scheduleService.findOne(secondSchedule).getDeleted());
        Assert.assertTrue(scheduleService.findOne(thirdSchedule).getDeleted());
    }

    private void createTimeUnitWithInterval(TimeUnitCode timeUnitCode){
        TimeUnit timeUnit = new TimeUnit();
        timeUnit.setCode(timeUnitCode);
        timeUnitService.save(timeUnit);
        TimeInterval timeInterval = new TimeInterval();
        timeInterval.setTimeUnit(timeUnit);
        timeInterval.setInterval(1L);
        timeIntervalService.save(timeInterval);
    }
}
