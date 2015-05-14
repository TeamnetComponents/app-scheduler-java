
package ro.teamnet.scheduler.repository;


import ro.teamnet.bootstrap.extend.AppRepository;

import ro.teamnet.scheduler.domain.Schedule; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository for the Schedule entity.
 */
public interface ScheduleRepository extends AppRepository<Schedule,Long>{


    @Override
    @Query("select schedule from Schedule schedule left join fetch schedule.timeInterval where schedule.id =:id")
    Schedule findOne(Long id);

}
