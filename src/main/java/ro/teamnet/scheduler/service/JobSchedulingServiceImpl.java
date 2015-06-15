package ro.teamnet.scheduler.service;

import org.joda.time.DateTime;
import org.json.JSONObject;
import org.quartz.*;
import org.quartz.impl.calendar.BaseCalendar;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.spi.OperableTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ro.teamnet.scheduler.domain.Schedule;
import ro.teamnet.scheduler.domain.ScheduledJob;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static ro.teamnet.scheduler.constants.QuartzSchedulingConstants.*;

@Service
public class JobSchedulingServiceImpl implements JobSchedulingService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    private Scheduler scheduler;

    @Inject
    private TaskService taskService;

    @Override
    public void createJob(ScheduledJob job) {
        JobKey jobKey = new JobKey(job.getJobName(), JOB_GROUP);

        try {
            Map<Integer, String> taskOptions = taskService.getTaskOptionsByQueuePosition(job.getId());
            createOrUpdateJob(job, jobKey, taskOptions);
        } catch (ClassNotFoundException | SchedulerException e) {
            e.printStackTrace();
        }
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
    private void createOrUpdateJob(ScheduledJob scheduledJob, JobKey jobKey, Map<Integer, String> taskOptions) throws ClassNotFoundException, SchedulerException {
        Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(scheduledJob.getQuartzJobClassName());
        scheduler.addJob(
                JobBuilder.newJob()
                        .ofType(jobClass)
                        .withIdentity(jobKey)
                        .usingJobData(JOB_ID, scheduledJob.getId())
                        .usingJobData(JOB_OPTIONS, new JSONObject(taskOptions).toString())
                        .usingJobData(JOB_CLASS, scheduledJob.getQuartzJobClassName())
                        .usingJobData(JOB_VERSION, scheduledJob.getVersion())
                        .build(),
                true, true);
    }

    @Override
    public void updateJob(ScheduledJob job) {
        JobKey jobKey = new JobKey(job.getJobName(), JOB_GROUP);
        try {
            unscheduleJob(job);
            Map<Integer, String> taskOptions = taskService.getTaskOptionsByQueuePosition(job.getId());
            createOrUpdateJob(job, jobKey, taskOptions);
        } catch (ClassNotFoundException | SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes all triggers associated to the given job.
     *
     * @param scheduledJob the job
     * @throws SchedulerException
     */
    private void unscheduleJob(ScheduledJob scheduledJob) throws SchedulerException {
        scheduler.unscheduleJobs(getTriggerKeysForScheduledJob(scheduledJob));
    }

    /**
     * Retrieves all trigger keys for a given job.
     *
     * @param scheduledJob the job
     * @return a list of quartz trigger keys associated with the job
     * @throws SchedulerException
     */
    private List<TriggerKey> getTriggerKeysForScheduledJob(ScheduledJob scheduledJob) throws SchedulerException {
        GroupMatcher<TriggerKey> matcher = GroupMatcher.<TriggerKey>triggerGroupEquals(scheduledJob.getTriggerGroup());
        ArrayList<TriggerKey> triggerKeys = new ArrayList<>(scheduler.getTriggerKeys(matcher));
        log.info("Job " + scheduledJob.getJobName() + " has the following triggers: " + triggerKeys);
        return triggerKeys;
    }

    @Override
    public void deleteJob(ScheduledJob job) {
        JobKey jobKey = new JobKey(job.getJobName(), JOB_GROUP);
        try {
            unscheduleJob(job);
            scheduler.resumeJob(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addTrigger(Schedule schedule) {
        JobKey jobKey = new JobKey(schedule.getScheduledJob().getJobName(), JOB_GROUP);
        try {
            scheduleJob(schedule, jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Schedules the given job.
     *
     * @param schedule the schedule
     * @param jobKey   Quartz job key
     * @throws SchedulerException
     */
    private void scheduleJob(Schedule schedule, JobKey jobKey) throws SchedulerException {
        TriggerKey triggerKey = new TriggerKey(schedule.getTriggerName(), schedule.getTriggerGroup());
        if (scheduler.checkExists(triggerKey)) {
            scheduler.rescheduleJob(triggerKey, createTrigger(schedule, jobKey, triggerKey));

        } else {
            scheduler.scheduleJob(createTrigger(schedule, jobKey, triggerKey));
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
        CronScheduleBuilder schedBuilder = CronScheduleBuilder.cronSchedule(schedule.getCron());
        Date triggerStartTime = getFireTimeAfter(schedBuilder);

        TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
                .forJob(jobKey)
                .withIdentity(triggerKey)
                .withSchedule(applyMisfireInstruction(schedBuilder, schedule))
                .startAt(triggerStartTime)
                .usingJobData(TRIGGER_ID, schedule.getId())
                .usingJobData(TRIGGER_VERSION, schedule.getVersion());

        Date triggerEndDate = getTriggerEndDate(schedule, (OperableTrigger) triggerBuilder.build());
        if (triggerEndDate != null) {
            triggerBuilder.endAt(triggerEndDate);
        }

        return triggerBuilder.build();
    }

    private CronScheduleBuilder applyMisfireInstruction(CronScheduleBuilder scheduleBuilder, Schedule schedule) {
        //TODO read MisfiryPolicy from schedule and apply to scheduleBuilder
        return scheduleBuilder.withMisfireHandlingInstructionDoNothing();
    }

    private Date getFireTimeAfter(CronScheduleBuilder schedBuilder) {
        return schedBuilder.build().getFireTimeAfter(new Date());
    }

    /**
     * Computes a trigger end date based on the scheduler end time and allowed repetition count.
     *
     * @param schedule the schedule
     * @param trigger  the trigger
     * @return the trigger end date, or {@code null} if trigger should be fired indefinitely
     */
    private Date getTriggerEndDate(Schedule schedule, OperableTrigger trigger) {
        DateTime endTime = schedule.getEndTime();

        if (schedule.getRepetitions() == null || schedule.getRepetitions() <= 0) {
            return endTime == null ? null : schedule.getEndTime().toDate();
        }

        Date endDateByRepetitions = getEndDateByRepetitions(trigger, schedule.getRepetitions());
        if (endTime == null) {
            return endDateByRepetitions;
        }
        return endTime.isBefore(endDateByRepetitions.getTime()) ? endTime.toDate() : endDateByRepetitions;
    }

    /**
     * Compute an end date based on a trigger and a given number of repetitions.
     *
     * @param trigger     the trigger
     * @param repetitions number of allowed repetitions
     * @return an end date for the trigger
     */
    private Date getEndDateByRepetitions(OperableTrigger trigger, Long repetitions) {
        return TriggerUtils.computeEndTimeToAllowParticularNumberOfFirings(
                trigger,
                new BaseCalendar(java.util.Calendar.getInstance().getTimeZone()),
                repetitions.intValue()
        );
    }

    @Override
    public void deleteTrigger(Schedule schedule) {
        TriggerKey triggerKey = new TriggerKey(schedule.getTriggerName(), schedule.getTriggerGroup());
        try {
            if (scheduler.checkExists(triggerKey)) {
                scheduler.unscheduleJob(triggerKey);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
