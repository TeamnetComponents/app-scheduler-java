package ro.teamnet.scheduler.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import ro.teamnet.bootstrap.extend.AppRepository;

import ro.teamnet.scheduler.domain.ExecutionData; 

/**
 * Spring Data JPA repository for the ExecutionData entity.
 */
public interface ExecutionDataRepository extends AppRepository<ExecutionData,Long>{

    @Override
    @Query("select executionData from ExecutionData executionData " +
            "left join fetch executionData.configuration " +
            "left join fetch executionData.scheduledJobExecution " +
            "where executionData.id =:id")
    ExecutionData findOne(@Param("id") Long id);
    
    @Query("select executionData from ExecutionData executionData " +
            "left join fetch executionData.configuration " +
            "left join fetch executionData.scheduledJobExecution " +
            "where executionData.configuration.configurationId =:configurationId " +
            "and executionData.configuration.type =:configurationType " +
            "and executionData.scheduledJobExecution.id =:executionId")
    ExecutionData findByConfigurationIdAndTypeAndExecutionId(@Param("configurationId") Long configurationId,
                                                             @Param("configurationType") String configurationType,
                                                             @Param("executionId") Long executionId);

}
