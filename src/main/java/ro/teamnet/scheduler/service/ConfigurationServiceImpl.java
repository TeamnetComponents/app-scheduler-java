
package ro.teamnet.scheduler.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ro.teamnet.bootstrap.service.AbstractServiceImpl;
import ro.teamnet.scheduler.domain.Configuration;
import ro.teamnet.scheduler.domain.ScheduledJob;
import ro.teamnet.scheduler.dto.ConfigurationDTO;
import ro.teamnet.scheduler.repository.ConfigurationRepository;

import javax.inject.Inject;
import java.util.List;

@Service
public class ConfigurationServiceImpl extends AbstractServiceImpl<Configuration, Long> implements ConfigurationService {

    private final Logger log = LoggerFactory.getLogger(ConfigurationServiceImpl.class);

    @Inject
    public ConfigurationServiceImpl(ConfigurationRepository repository) {
        super(repository);
    }

    private ConfigurationRepository getConfigurationRepository() {
        return (ConfigurationRepository) getRepository();
    }

    @Override
    public List<Configuration> findByScheduledJobId(Long scheduledJobId) {
        return getConfigurationRepository().findByScheduledJobId(scheduledJobId);
    }

    @Override
    public ScheduledJob findBaseJobByConfiguration(ConfigurationDTO configurationDTO) {
        return getConfigurationRepository().findByConfigurationIdAndType(configurationDTO.getConfigurationId(),
                configurationDTO.getType()).getScheduledJob();
    }

    @Override
    public void deleteByConfigurationIdAndType(Long configurationId,String type) {
        Configuration configuration = getConfigurationRepository().findByConfigurationIdAndType(configurationId,type);

        getConfigurationRepository().delete(configuration);
    }


}
