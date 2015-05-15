package ro.teamnet.scheduler.repository;


import org.springframework.data.jpa.repository.Query;
import ro.teamnet.bootstrap.extend.AppRepository;
import ro.teamnet.scheduler.domain.SchedulableJob;

/**
 * Spring Data JPA repository for the SchedulableJob entity.
 */
public interface SchedulableJobRepository extends AppRepository<SchedulableJob, Long> {


    @Override
    @Query("select schedulableJob from SchedulableJob schedulableJob left join fetch schedulableJob.task where schedulableJob.id =:id")
    SchedulableJob findOne(Long id);

}
