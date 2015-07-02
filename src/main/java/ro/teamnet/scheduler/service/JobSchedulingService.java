package ro.teamnet.scheduler.service;

import ro.teamnet.scheduler.domain.Schedule;
import ro.teamnet.scheduler.domain.ScheduledJob;

/**
 * A service handling job scheduling. Service is called by the {@link ro.teamnet.scheduler.aop.JobSchedulingAspect}.
 */
public interface JobSchedulingService {

    /**
     * Handles job scheduling when a {@link ScheduledJob} is saved.
     *
     * @param job the scheduled job
     */
    void onScheduledJobSave(ScheduledJob job);

    /**
     * Handles job scheduling when a {@link ScheduledJob} is deleted.
     *
     * @param jobId the job id
     */
    void onScheduledJobDelete(Long jobId);

    /**
     * Handles job scheduling when a {@link Schedule} is saved.
     *
     * @param schedule the schedule
     */
    void onScheduleSave(Schedule schedule);

    /**
     * Handles job scheduling when a {@link Schedule} is deleted.
     *
     * @param scheduleId the schedule id
     */
    void onScheduleDelete(Long scheduleId);
}
