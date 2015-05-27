package ro.teamnet.scheduler.service;

import org.json.JSONObject;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ro.teamnet.scheduler.domain.Schedule;
import ro.teamnet.scheduler.domain.ScheduledJob;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * Scheduling service. Reads stored jobs from the database and passes them to the Quartz scheduler.
 */
@Service
public class QuartzService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Interval (in milliseconds) for scanning the db and scheduling jobs.
     */
    public static final long JOB_SCHEDULING_INTERVAL = 30000L;
    @Inject
    private Scheduler scheduler;

    @Inject
    private ScheduleService scheduleService;

    @Inject
    private ScheduledJobService scheduledJobService;
    @Inject
    private TaskService taskService;

    /**
     * Scans the db for jobs and schedules them.
     */
    @Scheduled(fixedRate = JOB_SCHEDULING_INTERVAL)
    private void setupJobs() {
        List<ScheduledJob> scheduledJobs = scheduledJobService.findAll();
        for (ScheduledJob scheduledJob : scheduledJobs) {
            log.info(scheduledJob.toString());
            Map<Integer, String> taskOptions = taskService.getTaskOptionsByQueuePosition(scheduledJob.getId());
            if (taskOptions.size() == 0) {
                log.info("Job has no tasks! Scheduling skipped.");
                continue;
            }

            JobDetail jobDetail;
            try {
                Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(scheduledJob.getQuartzJobClassName());
                jobDetail = JobBuilder.newJob()
                        .ofType(jobClass)
                        .withIdentity(scheduledJob.getId().toString())
                        .usingJobData("options", new JSONObject(taskOptions).toString())
                        .build();
            } catch (ClassNotFoundException e) {
                log.info("Class " + scheduledJob.getQuartzJobClassName() + " was not found! Scheduling skipped.");
                continue;
            }

            try {
                scheduler.addJob(jobDetail, true, true);
            } catch (SchedulerException e) {
                e.printStackTrace();
            }

            List<Schedule> schedules = scheduleService.findByScheduledJobId(scheduledJob.getId());
            for (Schedule schedule : schedules) {
                log.info(schedule.toString());
                if (schedule.getDeleted() || !schedule.getActive() || schedule.getCron() == null) {
                    log.info("Schedule not active! Scheduling skipped.");
                    continue;
                }
                Trigger trigger = TriggerBuilder.newTrigger()
                        .forJob(jobDetail.getKey())
                        .withIdentity(schedule.getId().toString())
                        .withSchedule(CronScheduleBuilder.cronSchedule(schedule.getCron()))
                        .build();

                try {
                    if (scheduler.checkExists(trigger.getKey())) {
                        //TODO : de adaugat o versionare in tabela Schedule pentru a nu reseta triggerul decat daca s-a modificat cron-ul
                        scheduler.rescheduleJob(trigger.getKey(), trigger);
                    } else {
                        scheduler.scheduleJob(trigger);
                    }
                } catch (SchedulerException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}