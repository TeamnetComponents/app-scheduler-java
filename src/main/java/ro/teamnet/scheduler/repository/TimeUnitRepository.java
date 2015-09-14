package ro.teamnet.scheduler.repository;


import ro.teamnet.bootstrap.extend.AppRepository;
import ro.teamnet.scheduler.domain.TimeUnit;
import ro.teamnet.scheduler.enums.TimeUnitCode;

/**
 * Spring Data JPA repository for the TimeUnit entity.
 */
public interface TimeUnitRepository extends AppRepository<TimeUnit, Long> {

    TimeUnit findByCode(TimeUnitCode code);
}
