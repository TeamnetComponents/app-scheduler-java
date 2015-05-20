package ro.teamnet.scheduler.repository;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ro.teamnet.bootstrap.extend.AppRepository;
import ro.teamnet.scheduler.domain.Task;

/**
 * Spring Data JPA repository for the Task entity.
 */
public interface TaskRepository extends AppRepository<Task, Long> {


    @Override
    @Query("select task from Task task left join fetch task.scheduledJob where task.id =:id")
    Task findOne(@Param("id") Long id);

}
