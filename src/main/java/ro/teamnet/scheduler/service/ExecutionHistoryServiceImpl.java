package ro.teamnet.scheduler.service;

import org.springframework.stereotype.Service;
import ro.teamnet.bootstrap.extend.AppPage;
import ro.teamnet.bootstrap.extend.AppPageable;
import ro.teamnet.scheduler.dto.ConfigurationDTO;
import ro.teamnet.scheduler.dto.JobExecutionDTO;

import javax.inject.Inject;

/**
 * The public scheduler service implementation.
 */
@Service
public class ExecutionHistoryServiceImpl implements ExecutionHistoryService {

    @Inject
    private ScheduledJobExecutionService scheduledJobExecutionService;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    ExecutionDataService executionDataService;

    @Override
    public AppPage<JobExecutionDTO> findJobExecutions(AppPageable appPageable, ConfigurationDTO configurationDTO) {
        return scheduledJobExecutionService.findJobExecutionDTOs(appPageable,
                configurationService.findBaseJobByConfiguration(configurationDTO).getId());
    }

    @Override
    public Long getExecutionDataId(ConfigurationDTO configurationDTO, Long jobExecutionId) {
        return executionDataService.findByConfigurationIdAndTypeAndExecutionId(configurationDTO.getConfigurationId(),
                configurationDTO.getType(), jobExecutionId).getDataId();
    }


}
