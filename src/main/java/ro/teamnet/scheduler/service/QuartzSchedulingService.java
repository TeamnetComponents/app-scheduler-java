package ro.teamnet.scheduler.service;

import org.joda.time.DateTime;
import org.json.JSONObject;
import org.quartz.*;
import org.quartz.impl.calendar.BaseCalendar;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.spi.OperableTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ro.teamnet.scheduler.domain.Schedule;
import ro.teamnet.scheduler.domain.ScheduledJob;

import javax.inject.Inject;
import java.util.*;
import java.util.Calendar;

import static ro.teamnet.scheduler.constants.QuartzSchedulingConstants.*;

/**
 * Scheduling service. Reads stored jobs from the database and passes them to the Quartz scheduler.
 */
@Service
public class QuartzSchedulingService {

    private final Logger log = LoggerFactory.getLogger(getClass());

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
     * TODO: handle exceptions
     * TODO: handle failing jobs
     */
    @Scheduled(fixedRate = JOB_SCHEDULING_INTERVAL)
    private void setupScheduledJobs() throws SchedulerException, ClassNotFoundException {
        List<ScheduledJob> scheduledJobs = scheduledJobService.findAll();
        Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.<JobKey>groupEquals(JOB_GROUP));
        log.info("Current jobs: " + jobKeys);

        for (ScheduledJob scheduledJob : scheduledJobs) {
            log.info("Begin setup of job: " + scheduledJob);
            JobKey jobKey = new JobKey(scheduledJob.getJobName(), JOB_GROUP);
            Map<Integer, String> taskOptions = taskService.getTaskOptionsByQueuePosition(scheduledJob.getId());
            if (!jobKeys.contains(jobKey)) {
                createAndScheduleNewJob(scheduledJob, jobKey, taskOptions);
            } else {
                updateAndScheduleExistingJob(scheduledJob, jobKey, taskOptions);
            }
        }
    }

    /**
     * Creates and schedules a new job.
     *
     * @param scheduledJob the job to schedule
     * @param jobKey       quartz job key
     * @param taskOptions  the task options used at execution time
     * @throws ClassNotFoundException
     * @throws SchedulerException
     */
    private void createAndScheduleNewJob(ScheduledJob scheduledJob, JobKey jobKey, Map<Integer, String> taskOptions) throws ClassNotFoundException, SchedulerException {
        if (!scheduledJob.getDeleted()) {
            log.info("Create new job : " + jobKey);
            createOrUpdateJob(scheduledJob, jobKey, taskOptions);
            scheduleJob(scheduledJob, jobKey);
        }
    }

    /**
     * Updates and schedules an existing job.
     *
     * @param scheduledJob the job to update
     * @param jobKey       Quartz job key
     * @param taskOptions  the task options used at execution time
     * @throws ClassNotFoundException
     * @throws SchedulerException
     */
    private void updateAndScheduleExistingJob(ScheduledJob scheduledJob, JobKey jobKey, Map<Integer, String> taskOptions) throws SchedulerException, ClassNotFoundException {
        log.info("Update existing job : " + jobKey);
        if (scheduledJob.getDeleted()) {
            unscheduleJob(scheduledJob);
            scheduler.deleteJob(jobKey);
            return;
        }

        if (jobVersionChanged(scheduledJob, jobKey)) {
            unscheduleJob(scheduledJob);
            createOrUpdateJob(scheduledJob, jobKey, taskOptions);
        }
        scheduleJob(scheduledJob, jobKey);
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
                        .usingJobData(JOB_OPTIONS, new JSONObject(taskOptions).toString())
                        .usingJobData(JOB_CLASS, scheduledJob.getQuartzJobClassName())
                        .usingJobData(JOB_VERSION, scheduledJob.getVersion())
                        .build(),
                true, true);
    }

    /**
     * Schedules the given job.
     *
     * @param scheduledJob the job
     * @param jobKey       Quartz job key
     * @throws SchedulerException
     */
    private void scheduleJob(ScheduledJob scheduledJob, JobKey jobKey) throws SchedulerException {
        for (Schedule schedule : scheduleService.findByScheduledJobId(scheduledJob.getId())) {
            TriggerKey triggerKey = new TriggerKey(schedule.getTriggerName(), schedule.getTriggerGroup());
            if (scheduler.checkExists(triggerKey)) {
                if (!schedule.isValid()) {
                    scheduler.unscheduleJob(triggerKey);
                } else if (triggerVersionChanged(schedule, triggerKey)) {
                    scheduler.rescheduleJob(triggerKey, createTrigger(schedule, jobKey, triggerKey));
                }
            } else if (schedule.isValid()) {
                scheduler.scheduleJob(createTrigger(schedule, jobKey, triggerKey));
            }

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
        return new ArrayList<>(scheduler.getTriggerKeys(matcher));
    }

    /**
     * Checks if the current job version is different from the version stored in the Quartz job details.
     * If the version changed, the job should be updated.
     *
     * @param scheduledJob the job
     * @param jobKey       Quartz job key
     * @return {@code true} if the job version has changed
     * @throws SchedulerException
     */
    private boolean jobVersionChanged(ScheduledJob scheduledJob, JobKey jobKey) throws SchedulerException {
        return !scheduler.getJobDetail(jobKey).getJobDataMap().get(JOB_VERSION).equals(scheduledJob.getVersion());
    }

    /**
     * Checks if the current schedule version is different from the version stored in the Quartz trigger details.
     * If the version changed, the job should be rescheduled.
     *
     * @param schedule   the schedule
     * @param triggerKey Quartz trigger key
     * @return {@code true} if the trigger version has changed
     * @throws SchedulerException
     */
    private boolean triggerVersionChanged(Schedule schedule, TriggerKey triggerKey) throws SchedulerException {
        return !scheduler.getTrigger(triggerKey).getJobDataMap().get(TRIGGER_VERSION).equals(schedule.getVersion());
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
        TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
                .forJob(jobKey)
                .withIdentity(triggerKey)
                .withSchedule(CronScheduleBuilder.cronSchedule(schedule.getCron()))
                .startAt(schedule.getStartTime().toDate())
                .usingJobData(TRIGGER_VERSION, schedule.getVersion());

        Date triggerEndDate = getTriggerEndDate(schedule, (OperableTrigger) triggerBuilder.build());
        if (triggerEndDate != null) {
            triggerBuilder.endAt(triggerEndDate);
        }

        return triggerBuilder.build();
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
                new BaseCalendar(Calendar.getInstance().getTimeZone()),
                repetitions.intValue()
        );
    }
}