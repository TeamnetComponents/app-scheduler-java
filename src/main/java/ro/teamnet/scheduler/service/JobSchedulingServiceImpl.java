package ro.teamnet.scheduler.service;

import org.json.JSONObject;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ro.teamnet.scheduler.domain.Schedule;
import ro.teamnet.scheduler.domain.ScheduledJob;
import ro.teamnet.scheduler.domain.Task;

import javax.inject.Inject;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static ro.teamnet.scheduler.constants.QuartzSchedulingConstants.*;

@Service
public class JobSchedulingServiceImpl implements JobSchedulingService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    private Scheduler scheduler;

    @Inject
    private ScheduledJobService scheduledJobService;

    @Inject
    private ScheduleService scheduleService;

    @Inject
    private TaskService taskService;

    @Override
    public void onScheduledJobSave(ScheduledJob job) {
        JobKey jobKey = job.getJobKey();
        try {
            Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.<JobKey>jobGroupEquals(job.getJobGroup()));
            scheduler.deleteJobs(new ArrayList<JobKey>(jobKeys));
            if (job.getDeleted()) {
                return;
            }
            Map<Integer, String> taskOptions = taskService.getTaskOptionsByQueuePosition(job.getId());
            createJob(job, jobKey, taskOptions);
            scheduleJob(job);
        } catch (ClassNotFoundException | SchedulerException e) {
            log.error("Error on saving job " + jobKey, e);
        }
    }

    @Override
    public void onScheduledJobDelete(Long jobId) {
        JobKey jobKey = scheduledJobService.findOne(jobId).getJobKey();
        try {
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            log.error("Error on deleting job " + jobKey, e);
        }
    }

    @Override
    public void onScheduleSave(Schedule schedule) {
        try {
            JobKey jobKey = schedule.getScheduledJob().getJobKey();
            if (!scheduler.checkExists(jobKey)) {
                return;
            }
            Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(schedule.getTriggerGroup()));
            scheduler.unscheduleJobs(new ArrayList<TriggerKey>(triggerKeys));
            scheduleJob(jobKey, schedule);
        } catch (SchedulerException e) {
            e.printStackTrace();
            log.error("Error on scheduling " + schedule.getTriggerKey(), e);
        }
    }

    @Override
    public void onScheduleDelete(Long scheduleId) {
        TriggerKey triggerKey = scheduleService.findOne(scheduleId).getTriggerKey();
        try {
            if (scheduler.checkExists(triggerKey)) {
                scheduler.unscheduleJob(triggerKey);
            }
        } catch (SchedulerException e) {
            log.error("Error on unscheduling " + triggerKey, e);
        }
    }

    @Override
    public void onTaskSave(Task task) {
        onScheduledJobSave(task.getScheduledJob());
    }

    @Override
    public void onTaskDelete(Long taskId) {
        onScheduledJobSave(taskService.findOne(taskId).getScheduledJob());
    }

    /**
     * Creates a new Quartz job based on the given {@link ScheduledJob}.
     *
     * @param scheduledJob the job
     * @param jobKey       Quartz job key
     * @param taskOptions  the task options used at execution time
     * @throws ClassNotFoundException
     * @throws SchedulerException
     */
    private void createJob(ScheduledJob scheduledJob, JobKey jobKey, Map<Integer, String> taskOptions) throws ClassNotFoundException, SchedulerException {
        Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(scheduledJob.getQuartzJobClassName());
        scheduler.addJob(
                JobBuilder.newJob()
                        .ofType(jobClass)
                        .withIdentity(jobKey)
                        .usingJobData(JOB_ID, scheduledJob.getId())
                        .usingJobData(JOB_OPTIONS, new JSONObject(taskOptions).toString())
                        .usingJobData(JOB_CLASS, scheduledJob.getQuartzJobClassName())
                        .usingJobData(JOB_VERSION, scheduledJob.getVersion())
                        .storeDurably()
                        .build(),
                false, false);
    }


    /**
     * Schedules the given job based on the {@link Schedule} definitions linked to it.
     *
     * @param job the job to be scheduled
     * @throws SchedulerException
     */
    private void scheduleJob(ScheduledJob job) {
        JobKey jobKey = job.getJobKey();
        for (Schedule schedule : scheduleService.findByScheduledJobId(job.getId())) {
            try {
                scheduleJob(jobKey, schedule);
            } catch (SchedulerException e) {
                log.error("Error on scheduling " + schedule.getTriggerKey(), e);
            }
        }
    }

    /**
     * Schedules the job with the given key using the given {@link Schedule} definition.
     *
     * @param jobKey   Quartz job key
     * @param schedule the schedule definition
     * @throws SchedulerException
     */
    private void scheduleJob(JobKey jobKey, Schedule schedule) throws SchedulerException {
        if (!schedule.isValid()) {
            return;
        }
        TriggerKey triggerKey = schedule.getTriggerKey();
        CronTrigger trigger = createTrigger(schedule, jobKey, triggerKey);

        if (trigger == null) {
            return;
        }

        if (scheduler.checkExists(triggerKey)) {
            scheduler.rescheduleJob(triggerKey, trigger);

        } else {
            scheduler.scheduleJob(trigger);
        }
    }

    /**
     * Create a new trigger based on the given schedule.
     *
     * @param schedule   the schedule
     * @param jobKey     Quartz job key
     * @param triggerKey Quartz trigger key
     * @return a trigger created for the schedule
     */
    private CronTrigger createTrigger(Schedule schedule, JobKey jobKey, TriggerKey triggerKey) {
        try {
            CronExpression cronExpression = new CronExpression(schedule.getCron());
            Date triggerStartDate = getTriggerStartDate(schedule);
            if (!cronExpression.isSatisfiedBy(triggerStartDate) && cronExpression.getNextValidTimeAfter(triggerStartDate) == null) {
                return null;
            }

            Date triggerEndDate = getTriggerEndDate(cronExpression, triggerStartDate, schedule.getEndTime() == null ? null : schedule.getEndTime().toDate(),
                    schedule.getRepetitions());

            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);

            TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
                    .forJob(jobKey)
                    .withIdentity(triggerKey)
                    .withSchedule(applyMisfireInstruction(cronScheduleBuilder, schedule))
                    .startAt(triggerStartDate)
                    .endAt(triggerEndDate)
                    .usingJobData(TRIGGER_ID, schedule.getId())
                    .usingJobData(TRIGGER_VERSION, schedule.getVersion());

            return triggerBuilder.build();
        } catch (ParseException e) {
            throw new RuntimeException("Cron expression '" + schedule.getCron() + "' is not valid.", e);
        }
    }

    private Date getTriggerStartDate(Schedule schedule) {
        Date triggerStartDate = new Date();
        if (triggerStartDate.before(schedule.getStartTime().toDate())) {
            triggerStartDate = schedule.getStartTime().toDate();
        }
        return triggerStartDate;
    }

    /**
     * Computes a trigger end date based on a cron expression, the run interval and allowed number of repetitions.
     *
     * @param cronExpression the cron expression
     * @param startDate      the start of the run interval
     * @param endDate        the end of the run interval - {@code null} when the run interval is indefinite
     * @param repetitions    the number of repetitions allowed for the trigger - when {@code null} or less than 1,
     *                       an unlimited number of repetitions should be allowed
     * @return the date at which the trigger should stop firing.
     */
    private Date getTriggerEndDate(CronExpression cronExpression, Date startDate, Date endDate, Long repetitions) {
        if (repetitions == null || repetitions <= 0) {
            return endDate;
        }
        long repetitionCountdown = repetitions;
        Date nextValidTime = startDate;

        if (cronExpression.isSatisfiedBy(startDate)) {
            repetitionCountdown--;
        }

        while (repetitionCountdown > 0) {
            nextValidTime = cronExpression.getNextValidTimeAfter(nextValidTime);
            repetitionCountdown--;
        }

        if (nextValidTime == null || (endDate != null && endDate.before(nextValidTime))) {
            return endDate;
        }

        return nextValidTime;
    }

    private CronScheduleBuilder applyMisfireInstruction(CronScheduleBuilder scheduleBuilder, Schedule schedule) {
        switch (schedule.getMisfirePolicy()){
            case FIRE_ONCE: return scheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
            case FIRE_ALL: return scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
            default:
                return scheduleBuilder.withMisfireHandlingInstructionDoNothing();
        }
    }
}