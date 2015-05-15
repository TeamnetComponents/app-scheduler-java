package ro.teamnet.scheduler.repository;


import ro.teamnet.bootstrap.extend.AppRepository;
import ro.teamnet.scheduler.domain.Task;

/**
 * Spring Data JPA repository for the Task entity.
 */
public interface TaskRepository extends AppRepository<Task, Long> {


}
