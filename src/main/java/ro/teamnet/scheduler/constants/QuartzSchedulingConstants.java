package ro.teamnet.scheduler.constants;

/**
 * Created by Oana.Mihai on 5/28/2015.
 */
public class QuartzSchedulingConstants {
    /**
     * Interval (in milliseconds) for polling a job plugin for status updates.
     */
    public static final long JOB_POLLING_INTERVAL = 30000L;
    /**
     * Group name used for all jobs based on the ScheduledJob entity.
     */
    public static final String JOB_GROUP = "SCHEDULED_JOBS";

    /**
     * Job data map property key for the scheduled job id.
     */
    public static final String JOB_ID = "JOB_ID";

    /**
     * Trigger data map property key for the trigger (schedule) id.
     */
    public static final String TRIGGER_ID = "TRIGGER_ID";

    /**
     * Trigger data map property key for the trigger (schedule) version.
     */
    public static final String TRIGGER_VERSION = "TRIGGER_VERSION";
}
