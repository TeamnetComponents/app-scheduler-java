package ro.teamnet.scheduler.service;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.teamnet.scheduler.constants.QuartzSchedulingConstants;
import ro.teamnet.scheduler.domain.Schedule;
import ro.teamnet.scheduler.domain.ScheduledJob;
import ro.teamnet.scheduler.job.AppJob;

import javax.inject.Inject;
import java.text.ParseException;
import java.util.Date;

import static ro.teamnet.scheduler.constants.QuartzSchedulingConstants.JOB_ID;
import static ro.teamnet.scheduler.constants.QuartzSchedulingConstants.TRIGGER_ID;

@Service
@Transactional
public class JobSchedulingServiceImpl implements JobSchedulingService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    private Scheduler scheduler;

    @Inject
    private ScheduleService scheduleService;

    @Override
    public void onScheduledJobSave(ScheduledJob job) {
        JobKey jobKey = getJobKey(job.getId());
        try {
            if (scheduler.checkExists(jobKey)) {
                if (job.getDeleted()) {
                    scheduler.deleteJob(jobKey);
                }
                return;
            }
            if (job.getDeleted()) {
                return;
            }
            createJob(jobKey, job.getId());
            scheduleJob(jobKey, job.getId());
        } catch (ClassNotFoundException | SchedulerException e) {
            log.error("Error on saving job " + jobKey, e);
        }
    }

    /**
     * Creates a job key for the job with the given id.
     *
     * @param jobId the job id
     * @return the job key
     */
    private JobKey getJobKey(Long jobId) {
        return new JobKey(getJobName(jobId), QuartzSchedulingConstants.JOB_GROUP);
    }

    /**
     * Provides a unique name for the job with the given id.
     *
     * @param jobId the job id
     * @return a unique job name to be used when creating the {@link JobKey}
     */
    private String getJobName(Long jobId) {
        return "Job_" + jobId;
    }


    @Override
    public void onScheduledJobDelete(Long jobId) {
        JobKey jobKey = getJobKey(jobId);
        try {
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            log.error("Error on deleting job " + jobKey, e);
        }
    }

    @Override
    public void onScheduleSave(Schedule schedule) {
        JobKey jobKey = getJobKey(schedule.getScheduledJob().getId());
        TriggerKey triggerKey = getTriggerKey(jobKey, schedule.getId());
        try {
            if (!scheduler.checkExists(jobKey)) {
                log.debug("Job " + jobKey + " not found. Scheduling " + triggerKey + " was skipped.");
                return;
            }
            scheduleJob(jobKey, triggerKey, schedule);
        } catch (SchedulerException e) {
            log.error("Error on scheduling " + triggerKey + " for job " + jobKey, e);
        }
    }

    /**
     * Creates a trigger key for the schedule with the given id, linked to the given job key.
     *
     * @param jobKey     the job key
     * @param scheduleId the schedule id
     * @return the trigger key
     */
    private TriggerKey getTriggerKey(JobKey jobKey, Long scheduleId) {
        String triggerGroup = jobKey.getName();
        return new TriggerKey(getTriggerName(scheduleId), triggerGroup);
    }

    /**
     * Provides a unique trigger name for the schedule with the given id.
     *
     * @param scheduleId the schedule id
     * @return a unique trigger name to be used when creating the {@link TriggerKey}
     */
    private String getTriggerName(Long scheduleId) {
        return "Schedule_" + scheduleId;
    }

    @Override
    public void onScheduleDelete(Long scheduleId) {
        Schedule schedule = scheduleService.findOne(scheduleId);
        JobKey jobKey = getJobKey(schedule.getScheduledJob().getId());
        TriggerKey triggerKey = getTriggerKey(jobKey, scheduleId);
        try {
            if (scheduler.checkExists(triggerKey)) {
                scheduler.unscheduleJob(triggerKey);
            }
        } catch (SchedulerException e) {
            log.error("Error on unscheduling " + triggerKey, e);
        }
    }

    /**
     * Creates a new Quartz job based on the given {@link ScheduledJob}.
     *
     * @param jobKey the Quartz job key
     * @param jobId  the job id
     * @throws ClassNotFoundException
     * @throws SchedulerException
     */
    private void createJob(JobKey jobKey, Long jobId) throws ClassNotFoundException, SchedulerException {
        scheduler.addJob(
                JobBuilder.newJob()
                        .ofType(AppJob.class)
                        .withIdentity(jobKey)
                        .usingJobData(JOB_ID, jobId)
                        .storeDurably()
                        .requestRecovery()
                        .build(),
                false, false);
    }


    /**
     * Schedules the given job based on the {@link Schedule} definitions linked to it.
     *
     * @param jobKey the Quartz job key
     * @param jobId  the job id
     */
    private void scheduleJob(JobKey jobKey, Long jobId) {
        for (Schedule schedule : scheduleService.findByScheduledJobId(jobId)) {
            TriggerKey triggerKey = getTriggerKey(jobKey, schedule.getId());
            try {
                scheduleJob(jobKey, triggerKey, schedule);
            } catch (SchedulerException e) {
                log.error("Error on scheduling " + triggerKey, e);
            }
        }
    }

    /**
     * Schedules the job with the given key using the given {@link Schedule} definition.
     *
     * @param jobKey     Quartz job key
     * @param triggerKey Quartz trigger key
     * @param schedule   the schedule definition
     * @throws SchedulerException
     */
    private void scheduleJob(JobKey jobKey, TriggerKey triggerKey, Schedule schedule) throws SchedulerException {
        if (!schedule.isValid()) {
            if (scheduler.checkExists(triggerKey)) {
                scheduler.unscheduleJob(triggerKey);
            }
            return;
        }
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
            if (!cronExpression.isSatisfiedBy(triggerStartDate)
                    && cronExpression.getNextValidTimeAfter(triggerStartDate) == null) {
                return null;
            }

            Date scheduleEndDate = schedule.getEndTime() == null ? null : schedule.getEndTime().toDate();
            Date triggerEndDate = getTriggerEndDate(cronExpression, triggerStartDate, scheduleEndDate, schedule.getRepetitions());

            TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
                    .forJob(jobKey)
                    .withIdentity(triggerKey)
                    .withSchedule(getCronScheduleBuilder(cronExpression, schedule))
                    .startAt(triggerStartDate)
                    .endAt(triggerEndDate)
                    .usingJobData(TRIGGER_ID, schedule.getId());

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

    /**
     * Creates a {@link CronScheduleBuilder} based on the given cron expression and schedule details.
     *
     * @param cronExpression the cron expression
     * @param schedule       the scheduling information
     * @return a cron schedule builder
     */
    private CronScheduleBuilder getCronScheduleBuilder(CronExpression cronExpression, Schedule schedule) {

        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);

        switch (schedule.getMisfirePolicy()) {
            case FIRE_ONCE:
                return scheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
            case FIRE_ALL:
                return scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
            default:
                return scheduleBuilder.withMisfireHandlingInstructionDoNothing();
        }
    }
}