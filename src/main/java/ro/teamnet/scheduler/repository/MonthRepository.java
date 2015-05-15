package ro.teamnet.scheduler.repository;


import ro.teamnet.bootstrap.extend.AppRepository;
import ro.teamnet.scheduler.domain.Month;

/**
 * Spring Data JPA repository for the Month entity.
 */
public interface MonthRepository extends AppRepository<Month, Long> {

}
