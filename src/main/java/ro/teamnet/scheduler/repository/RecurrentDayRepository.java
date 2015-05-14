
package ro.teamnet.scheduler.repository;


import ro.teamnet.bootstrap.extend.AppRepository;

import ro.teamnet.scheduler.domain.RecurrentDay; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository for the RecurrentDay entity.
 */
public interface RecurrentDayRepository extends AppRepository<RecurrentDay,Long>{


    @Override
    @Query("select recurrentDay from RecurrentDay recurrentDay left join fetch recurrentDay.schedule where recurrentDay.id =:id")
    RecurrentDay findOne(Long id);

}
