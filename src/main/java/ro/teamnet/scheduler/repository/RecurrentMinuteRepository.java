
package ro.teamnet.scheduler.repository;


import ro.teamnet.bootstrap.extend.AppRepository;

import ro.teamnet.scheduler.domain.RecurrentMinute; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository for the RecurrentMinute entity.
 */
public interface RecurrentMinuteRepository extends AppRepository<RecurrentMinute,Long>{


    @Override
    @Query("select recurrentMinute from RecurrentMinute recurrentMinute left join fetch recurrentMinute.schedule where recurrentMinute.id =:id")
    RecurrentMinute findOne(Long id);

}
