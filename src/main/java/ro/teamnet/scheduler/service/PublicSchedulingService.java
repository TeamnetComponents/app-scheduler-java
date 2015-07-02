package ro.teamnet.scheduler.service;

import org.springframework.stereotype.Service;
import ro.teamnet.scheduler.domain.Configuration;
import ro.teamnet.scheduler.domain.ScheduledJob;
import ro.teamnet.scheduler.dto.ConfigurationDTO;

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
        scheduledJobService.delete(getSchedulingId(configurationDTO));
    }

    @Override
    public Long getSchedulingId(ConfigurationDTO configurationDTO) {
        return configurationService.findBaseJobByConfiguration(configurationDTO).getId();
    }
}
