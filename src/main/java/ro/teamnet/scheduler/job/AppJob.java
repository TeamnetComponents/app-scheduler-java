package ro.teamnet.scheduler.job;

import org.joda.time.DateTime;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.plugin.core.OrderAwarePluginRegistry;
import org.springframework.plugin.core.PluginRegistry;
import ro.teamnet.scheduler.domain.Configuration;
import ro.teamnet.scheduler.domain.ExecutionData;
import ro.teamnet.scheduler.domain.ScheduledJob;
import ro.teamnet.scheduler.domain.ScheduledJobExecution;
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
public class AppJob implements InterruptableJob {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private Long scheduledJobId;
    private JobExecutionContext context;
    private ScheduledJobExecution jobExecution;

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

    @Override
    public final void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        context = jobExecutionContext;
        scheduledJobId = context.getMergedJobDataMap().getLong(JOB_ID);

        ScheduledJob job = scheduledJobService.findOne(scheduledJobId);
        jobExecution = context.isRecovering()
                ? job.getScheduledJobExecution()
                : scheduledJobExecutionService.save(createExecution(job));

        try {
            startOrRecoverJob();
        } catch (Exception e) {
            try {
                interrupt();
            } catch (UnableToInterruptJobException interruptException) {
                log.error("Unable to interrupt", interruptException);
            }
            throw new JobExecutionException(e);
        }
    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        updateExecutionStatus(FAILED);
    }

    private void startOrRecoverJob() throws InterruptedException {
        JobExecutionStatus globalStatus = null;
        Map<Configuration, ExecutionService> supportedExecutionPlugins = getSupportedExecutionPlugins();
        for (Map.Entry<Configuration, ExecutionService> plugins : supportedExecutionPlugins.entrySet()) {
            Configuration configuration = plugins.getKey();
            ExecutionService executionService = plugins.getValue();
            Long dataId;

            if (context.isRecovering()) {
                dataId = recoverJob(configuration, executionService);
            } else {
                dataId = startJob(configuration, executionService);
            }

            JobExecutionStatus status = poll(executionService, dataId);

            globalStatus = combineStatuses(globalStatus, status);
            updateExecutionStatus(globalStatus);
        }
    }

    private Long startJob(Configuration configuration, ExecutionService executionService) {
        Long dataId = executionService.start(configuration.getConfigurationId());
        saveExecutionData(configuration, dataId);
        return dataId;
    }

    private Long recoverJob(Configuration configuration, ExecutionService executionService) {
        Long dataId = executionDataService.findByConfigurationIdAndTypeAndExecutionId(
                configuration.getConfigurationId(), configuration.getType(), jobExecution.getId()).getDataId();
        dataId = executionService.recover(configuration.getConfigurationId(), dataId);
        return dataId;
    }

    private JobExecutionStatus poll(ExecutionService executionService, Long dataId) throws InterruptedException {
        JobExecutionStatus status = executionService.getStatus(dataId);
        updateExecutionStatus(status);
        while (status.isTemporary()) {
            Thread.sleep(JOB_POLLING_INTERVAL);
            status = executionService.getStatus(dataId);
            updateExecutionStatus(status);
        }
        return status;
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

    private Map<Configuration, ExecutionService> getSupportedExecutionPlugins() {
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
