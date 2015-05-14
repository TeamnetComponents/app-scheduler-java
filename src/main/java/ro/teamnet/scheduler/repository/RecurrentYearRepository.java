
package ro.teamnet.scheduler.repository;


import ro.teamnet.bootstrap.extend.AppRepository;

import ro.teamnet.scheduler.domain.RecurrentYear; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository for the RecurrentYear entity.
 */
public interface RecurrentYearRepository extends AppRepository<RecurrentYear,Long>{


    @Override
    @Query("select recurrentYear from RecurrentYear recurrentYear left join fetch recurrentYear.schedule where recurrentYear.id =:id")
    RecurrentYear findOne(Long id);

}
