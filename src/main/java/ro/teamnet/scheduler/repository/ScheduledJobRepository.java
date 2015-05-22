package ro.teamnet.scheduler.repository;

import ro.teamnet.bootstrap.extend.AppRepository;
import ro.teamnet.scheduler.domain.ScheduledJob;

import java.util.List;

/**
 * Spring Data JPA repository for the ScheduledJob entity.
 */
public interface ScheduledJobRepository extends AppRepository<ScheduledJob, Long> {

    /**
     * Finds all scheduled jobs (skips deleted).
     * @return all scheduled jobs (minus the ones marked as deleted)
     */
    List<ScheduledJob> findByDeletedFalse();
}
