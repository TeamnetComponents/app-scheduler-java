package ro.teamnet.scheduler.repository;


import org.springframework.data.jpa.repository.Query;
import ro.teamnet.bootstrap.extend.AppRepository;
import ro.teamnet.scheduler.domain.Schedule;

/**
 * Spring Data JPA repository for the Schedule entity.
 */
public interface ScheduleRepository extends AppRepository<Schedule, Long> {


    @Override
    @Query("select schedule from Schedule schedule left join fetch schedule.timeInterval left join fetch schedule.schedulableJob where schedule.id =:id")
    Schedule findOne(Long id);

}
