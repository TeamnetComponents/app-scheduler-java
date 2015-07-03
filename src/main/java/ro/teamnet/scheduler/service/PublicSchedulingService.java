package ro.teamnet.scheduler.service;

import org.springframework.stereotype.Service;
import ro.teamnet.scheduler.domain.Configuration;
import ro.teamnet.scheduler.domain.ScheduledJob;
import ro.teamnet.scheduler.dto.ConfigurationDTO;
import ro.teamnet.scheduler.dto.SchedulingDTO;

import javax.inject.Inject;

@Service
public class PublicSchedulingService implements SchedulingService {
    @Inject
    ScheduledJobService scheduledJobService;

    @Inject
    ConfigurationService configurationService;

    @Override
    public void createJob(ConfigurationDTO configurationDTO) {
        ScheduledJob job = scheduledJobService.save(new ScheduledJob());
        Configuration configuration = new Configuration(configurationDTO);
        configuration.setScheduledJob(job);
        configurationService.save(configuration);
    }

    @Override
    public void deleteJob(ConfigurationDTO configurationDTO) {
        Long baseJobId = getSchedulingId(configurationDTO).getId();
        configurationService.delete(configurationDTO.getConfigurationId());
        scheduledJobService.delete(baseJobId);
    }

    @Override
    public SchedulingDTO getSchedulingId(ConfigurationDTO configurationDTO) {

        ScheduledJob job = configurationService.findBaseJobByConfiguration(configurationDTO);

        return new SchedulingDTO(job.getId(), job.getVersion());
    }
}
