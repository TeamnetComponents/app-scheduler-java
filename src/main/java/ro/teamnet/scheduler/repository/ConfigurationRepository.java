package ro.teamnet.scheduler.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import ro.teamnet.bootstrap.extend.AppRepository;

import ro.teamnet.scheduler.domain.Configuration;

import java.util.List;

/**
 * Spring Data JPA repository for the Configuration entity.
 */
public interface ConfigurationRepository extends AppRepository<Configuration,Long>{


    @Override
    @Query("select configuration from Configuration configuration left join fetch configuration.scheduledJob where configuration.id =:id")
    Configuration findOne(@Param("id") Long id);

    List<Configuration> findByScheduledJobId(Long scheduledJobId);

    Configuration findByConfigurationIdAndType(Long configurationId, String type);
}
