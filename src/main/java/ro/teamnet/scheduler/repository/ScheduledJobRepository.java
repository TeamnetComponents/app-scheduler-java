package ro.teamnet.scheduler.repository;

import ro.teamnet.bootstrap.extend.AppRepository;
import ro.teamnet.scheduler.domain.ScheduledJob;

/**
 * Spring Data JPA repository for the ScheduledJob entity.
 */
public interface ScheduledJobRepository extends AppRepository<ScheduledJob, Long> {


}
