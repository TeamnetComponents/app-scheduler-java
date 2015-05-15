package ro.teamnet.scheduler.repository;


import ro.teamnet.bootstrap.extend.AppRepository;
import ro.teamnet.scheduler.domain.DayOfWeek;

/**
 * Spring Data JPA repository for the DayOfWeek entity.
 */
public interface DayOfWeekRepository extends AppRepository<DayOfWeek, Long> {


}
