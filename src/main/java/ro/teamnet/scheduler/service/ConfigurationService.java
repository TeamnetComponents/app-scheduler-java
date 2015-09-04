
package ro.teamnet.scheduler.service;


import ro.teamnet.bootstrap.service.AbstractService;
import ro.teamnet.scheduler.domain.Configuration;
import ro.teamnet.scheduler.domain.ScheduledJob;
import ro.teamnet.scheduler.dto.ConfigurationDTO;

import java.util.List;


public interface ConfigurationService extends AbstractService<Configuration,Long>{

    List<Configuration> findByScheduledJobId(Long scheduledJobId);

    ScheduledJob findBaseJobByConfiguration(ConfigurationDTO configurationDTO);

    void deleteByConfigurationIdAndType(Long configurationId,String type);

    Configuration findByJobExecId(Long jobExecId);
}
