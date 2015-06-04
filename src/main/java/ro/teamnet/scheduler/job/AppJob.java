package ro.teamnet.scheduler.job;

import org.joda.time.DateTime;
import org.json.JSONObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import ro.teamnet.scheduler.constants.QuartzSchedulingConstants;
import ro.teamnet.scheduler.domain.ScheduledJobExecution;
import ro.teamnet.scheduler.service.ScheduledJobExecutionService;
import ro.teamnet.scheduler.service.ScheduledJobService;

import javax.inject.Inject;

/**
 * Created by Oana.Mihai on 6/3/2015.
 */
public abstract class AppJob implements Job {

    private String options;
    private Long scheduledJobId;

    @Inject
    private ScheduledJobService scheduledJobService;


    @Inject
    private ScheduledJobExecutionService scheduledJobExecutionService;

    @Override
    public final void execute(JobExecutionContext context) throws JobExecutionException {
        options = context.getMergedJobDataMap().getString(QuartzSchedulingConstants.JOB_OPTIONS);
        scheduledJobId = context.getMergedJobDataMap().getLong(QuartzSchedulingConstants.JOB_ID);

        ScheduledJobExecution execution = saveExecution(context);

        try {
            run(context);
            updateExecutionStatus(execution, JobExecutionStatus.FINISHED);
        } catch (RuntimeException e) {
            updateExecutionStatus(execution, JobExecutionStatus.FAILED);
            throw new JobExecutionException(e);
        }
    }

    private void updateExecutionStatus(ScheduledJobExecution execution, JobExecutionStatus status) {
        execution.setStatus(status);
        scheduledJobExecutionService.save(execution);
    }

    private ScheduledJobExecution saveExecution(JobExecutionContext context) {
        ScheduledJobExecution execution = new ScheduledJobExecution();
        execution.setActualFireTime(new DateTime(context.getFireTime()));
        execution.setScheduledFireTime(new DateTime(context.getScheduledFireTime()));
        execution.setLastFireTime(new DateTime(context.getPreviousFireTime()));
        execution.setNextFireTime(new DateTime(context.getNextFireTime()));
        execution.setState(new JSONObject(context.getMergedJobDataMap()).toString());
        execution.setStatus(JobExecutionStatus.RUNNING);
        execution.setScheduledJob(scheduledJobService.findOne(scheduledJobId));
        return scheduledJobExecutionService.save(execution);
    }

    protected abstract void run(JobExecutionContext context);

    public String getOptions() {
        return options;
    }

}
