package ro.teamnet.scheduler.job;

import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.plugin.core.OrderAwarePluginRegistry;
import org.springframework.plugin.core.PluginRegistry;
import ro.teamnet.scheduler.domain.*;
import ro.teamnet.scheduler.enums.JobExecutionStatus;
import ro.teamnet.scheduler.service.*;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ro.teamnet.scheduler.constants.QuartzSchedulingConstants.JOB_ID;
import static ro.teamnet.scheduler.constants.QuartzSchedulingConstants.JOB_POLLING_INTERVAL;
import static ro.teamnet.scheduler.enums.JobExecutionStatus.*;

/**
 * A schedulable job.
 */
public class AppJob implements Job {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private Long scheduledJobId;
    private JobExecutionContext context;

    @Inject
    private ScheduledJobService scheduledJobService;
    @Inject
    private ScheduledJobExecutionService scheduledJobExecutionService;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private ExecutionDataService executionDataService;

    @Inject
    private List<ExecutionService> allExecutionServices;

    private ScheduledJobExecution jobExecution;

    /**
     * {@inheritDoc}
     */
    @Override
    public final void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        context = jobExecutionContext;
        scheduledJobId = context.getMergedJobDataMap().getLong(JOB_ID);

        ScheduledJob job = scheduledJobService.findOne(scheduledJobId);
        createOrRecoverJobExecution(job);
        JobExecutionException jobExecutionException = null;
        JobExecutionStatus status = null;
        for (Map.Entry<Configuration, ExecutionService> plugins : getSupportedExecutionServicePlugins().entrySet()) {
            Configuration configuration = plugins.getKey();
            ExecutionService executionService = plugins.getValue();
            try {
                Long dataId = executionService.start(configuration.getConfigurationId());
                saveExecutionData(configuration, dataId);
                JobExecutionStatus pluginStatus = JobExecutionStatus.WAITING;
                while (pluginStatus.isTemporary()) {
                    pluginStatus = executionService.getStatus(dataId);
                    updateExecutionStatus(pluginStatus);
                    Thread.sleep(JOB_POLLING_INTERVAL);
                }
                status = combineStatuses(status, pluginStatus);
            } catch (Exception e) {
                log.error("An error was encountered while running job plugin " + executionService.toString(), e);
                status = FAILED;
                jobExecutionException = new JobExecutionException(e);
                break;
            }
        }
        updateExecutionStatus(status);

        if (jobExecutionException != null) {
            throw jobExecutionException;
        }
    }

    private ExecutionData saveExecutionData(Configuration configuration, Long dataId) {
        ExecutionData executionData = new ExecutionData();
        executionData.setConfiguration(configuration);
        executionData.setScheduledJobExecution(jobExecution);
        executionData.setDataId(dataId);
        return executionDataService.save(executionData);
    }


    private JobExecutionStatus combineStatuses(JobExecutionStatus oldStatus, JobExecutionStatus newStatus) {
        //TODO change logic for combining statuses?
        if (newStatus == null) {
            return oldStatus;
        }
        if (oldStatus == null || newStatus.equals(FAILED)) {
            return newStatus;
        }
        if (oldStatus.equals(FAILED) || oldStatus.equals(CANCELLED) || newStatus.equals(oldStatus)
                || newStatus.equals(FINISHED)) {
            return oldStatus;
        }

        return newStatus;
    }

    private void createOrRecoverJobExecution(ScheduledJob scheduledJob) {
        if (context.isRecovering()) {
            jobExecution = scheduledJob.getScheduledJobExecution();
        } else {
            jobExecution = scheduledJobExecutionService.save(createExecution(scheduledJob));
        }
    }

    private Map<Configuration, ExecutionService> getSupportedExecutionServicePlugins() {
        // TODO handle multiple execution service implementations supporting the same configuration type?
        PluginRegistry<ExecutionService, String> pluginRegistry = OrderAwarePluginRegistry.create(allExecutionServices);
        Map<Configuration, ExecutionService> supportedJobPlugins = new HashMap<>();
        for (Configuration configuration : configurationService.findByScheduledJobId(scheduledJobId)) {
            supportedJobPlugins.put(configuration, pluginRegistry.getPluginFor(configuration.getType()));
        }
        return supportedJobPlugins;
    }

    protected void updateExecutionStatus(JobExecutionStatus status) {
        scheduledJobExecutionService.updateExecutionStatus(jobExecution.getId(), status);
    }

    private ScheduledJobExecution createExecution(ScheduledJob scheduledJob) {
        ScheduledJobExecution execution = new ScheduledJobExecution();
        execution.setActualFireTime(new DateTime(context.getFireTime()));
        execution.setScheduledFireTime(new DateTime(context.getScheduledFireTime()));
        execution.setLastFireTime(new DateTime(context.getPreviousFireTime()));
        execution.setNextFireTime(new DateTime(context.getNextFireTime()));
        execution.setStatus(JobExecutionStatus.WAITING);
        execution.setScheduledJob(scheduledJob);
        return execution;
    }
}
