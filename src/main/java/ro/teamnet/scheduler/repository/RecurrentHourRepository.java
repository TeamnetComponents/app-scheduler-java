
package ro.teamnet.scheduler.repository;


import ro.teamnet.bootstrap.extend.AppRepository;

import ro.teamnet.scheduler.domain.RecurrentHour; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository for the RecurrentHour entity.
 */
public interface RecurrentHourRepository extends AppRepository<RecurrentHour,Long>{


    @Override
    @Query("select recurrentHour from RecurrentHour recurrentHour left join fetch recurrentHour.schedule where recurrentHour.id =:id")
    RecurrentHour findOne(Long id);

}
