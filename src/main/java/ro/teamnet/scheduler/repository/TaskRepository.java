package ro.teamnet.scheduler.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ro.teamnet.bootstrap.extend.AppRepository;
import ro.teamnet.scheduler.domain.Task;

import java.util.List;

/**
 * Spring Data JPA repository for the Task entity.
 */
public interface TaskRepository extends AppRepository<Task, Long> {


    @Override
    @Query("select task from Task task left join fetch task.scheduledJob where task.id =:id")
    Task findOne(@Param("id") Long id);

    /**
     * Finds all tasks for the given scheduled job id (skips deleted tasks).
     *
     * @param scheduledJobId - the scheduled job id
     * @return all the tasks associated with the scheduled job with the given id
     */
    List<Task> findByDeletedFalseAndScheduledJobId(Long scheduledJobId);

    /**
     * Finds all tasks (skips deleted).
     * @return all tasks (minus the ones marked as deleted)
     */
    List<Task> findByDeletedFalse();
}
