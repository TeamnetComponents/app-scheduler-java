package ro.teamnet.scheduler.job;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import ro.teamnet.scheduler.constants.QuartzSchedulingConstants;
import ro.teamnet.scheduler.domain.ScheduledJob;
import ro.teamnet.scheduler.domain.ScheduledJobExecution;
import ro.teamnet.scheduler.service.ScheduledJobExecutionService;
import ro.teamnet.scheduler.service.ScheduledJobService;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Oana.Mihai on 6/3/2015.
 */
public abstract class AppJob implements Job {

    private String options;
    private Map<Integer, String> taskOptions;
    private Long scheduledJobId;
    private JobExecutionContext context;
    private Long executionId;

    @Inject
    private ScheduledJobService scheduledJobService;
    @Inject
    private ScheduledJobExecutionService scheduledJobExecutionService;

    /**
     * {@inheritDoc}
     */
    @Override
    public final void execute(JobExecutionContext context) throws JobExecutionException {
        this.context = context;
        readContextData();
        ScheduledJob scheduledJob = scheduledJobService.findOne(scheduledJobId);
        ScheduledJobExecution execution = createExecution(scheduledJob);
        executionId = scheduledJobExecutionService.save(execution).getId();

        JobExecutionStatus status = null;
        try {
            status =  run();
        } catch (Exception e) {
            status = JobExecutionStatus.FAILED;
            e.printStackTrace();
            throw new JobExecutionException(e);
        } finally {
            updateExecutionStatus(status);
        }
    }

    protected void updateExecutionStatus(JobExecutionStatus status) {
        scheduledJobExecutionService.updateExecutionStatus(executionId, status);
    }

    /**
     *
     * @return a post-run status
     */
    protected abstract JobExecutionStatus run();

    private void readContextData() {
        options = context.getMergedJobDataMap().getString(QuartzSchedulingConstants.JOB_OPTIONS);
        readTaskOptions();
        scheduledJobId = context.getMergedJobDataMap().getLong(QuartzSchedulingConstants.JOB_ID);
    }

    private void readTaskOptions() {
        taskOptions = new HashMap<>();

        //convert JSON string to Map
        try {
            taskOptions = new ObjectMapper().readValue(getOptions(), new TypeReference<HashMap<Integer, String>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ScheduledJobExecution createExecution(ScheduledJob scheduledJob) {
        ScheduledJobExecution execution = new ScheduledJobExecution();
        execution.setActualFireTime(new DateTime(context.getFireTime()));
        execution.setScheduledFireTime(new DateTime(context.getScheduledFireTime()));
        execution.setLastFireTime(new DateTime(context.getPreviousFireTime()));
        execution.setNextFireTime(new DateTime(context.getNextFireTime()));
        execution.setState(new JSONObject(context.getMergedJobDataMap()).toString());
        execution.setStatus(JobExecutionStatus.WAITING);
        execution.setScheduledJob(scheduledJob);
        return execution;
    }


    public String getOptions() {
        return options;
    }

    public Map<Integer, String> getTaskOptions() {
        return taskOptions;
    }

    public JobExecutionContext getContext() {
        return context;
    }
}
