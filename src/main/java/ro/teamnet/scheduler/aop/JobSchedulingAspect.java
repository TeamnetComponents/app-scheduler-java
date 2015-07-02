package ro.teamnet.scheduler.aop;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.teamnet.scheduler.domain.Schedule;
import ro.teamnet.scheduler.domain.ScheduledJob;
import ro.teamnet.scheduler.service.JobSchedulingService;

import javax.inject.Inject;

@Aspect
public class JobSchedulingAspect {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    private JobSchedulingService jobSchedulingService;

    @AfterReturning(pointcut = "execution(public * ro.teamnet.scheduler.service.ScheduledJobService+.save(..))",
            returning = "job")
    public void afterScheduledJobSave(ScheduledJob job) {
        log.info("afterScheduledJobSave");
        jobSchedulingService.onScheduledJobSave(job);
    }

    @AfterReturning(pointcut = "execution(public * ro.teamnet.scheduler.service.ScheduleService+.save(..))",
            returning = "schedule")
    public void afterScheduleSave(Schedule schedule) {
        log.info("afterScheduleSave");
        jobSchedulingService.onScheduleSave(schedule);
    }


    @AfterReturning("execution(public * ro.teamnet.scheduler.service.ScheduledJobService+.delete(..)) && args(jobId,..)")
    public void afterScheduledDelete(Long jobId) {
        log.info("afterScheduledDelete");
        jobSchedulingService.onScheduledJobDelete(jobId);
    }

    @AfterReturning("execution(public * ro.teamnet.scheduler.service.ScheduleService+.delete(..)) && args(scheduleId,..)")
    public void afterScheduleDelete(Long scheduleId) {
        log.info("afterScheduleDelete");
        jobSchedulingService.onScheduleDelete(scheduleId);
    }
}

