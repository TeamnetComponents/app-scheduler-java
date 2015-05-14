
package ro.teamnet.scheduler.repository;


import ro.teamnet.bootstrap.extend.AppRepository;

import ro.teamnet.scheduler.domain.RecurrentTimeUnit; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository for the RecurrentTimeUnit entity.
 */
public interface RecurrentTimeUnitRepository extends AppRepository<RecurrentTimeUnit,Long>{


    @Override
    @Query("select recurrentTimeUnit from RecurrentTimeUnit recurrentTimeUnit left join fetch recurrentTimeUnit.timeUnit left join fetch recurrentTimeUnit.schedule where recurrentTimeUnit.id =:id")
    RecurrentTimeUnit findOne(Long id);

}
