package ro.teamnet.scheduler.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import ro.teamnet.bootstrap.extend.AppRepository;

import ro.teamnet.scheduler.domain.ScheduledJob;
import ro.teamnet.scheduler.domain.ScheduledJobExecution;

/**
 * Spring Data JPA repository for the ScheduledJobExecution entity.
 */
public interface ScheduledJobExecutionRepository extends AppRepository<ScheduledJobExecution,Long>{


    @Override
    @Query("select scheduledJobExecution from ScheduledJobExecution scheduledJobExecution left join fetch scheduledJobExecution.scheduledJob where scheduledJobExecution.id =:id")
    ScheduledJobExecution findOne(@Param("id") Long id);

    @Query("select scheduledJob from ScheduledJob scheduledJob, ScheduledJobExecution scheduledJobExecution where scheduledJobExecution.id =:id and scheduledJob.id = scheduledJobExecution.scheduledJob.id")
    //@Query("select scheduledJob from ScheduledJob scheduledJob where scheduledJob.id = :id")
    ScheduledJob findJobByExecutionId(@Param("id") Long id);

}
