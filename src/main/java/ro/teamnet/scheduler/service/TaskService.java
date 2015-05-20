package ro.teamnet.scheduler.service;


import ro.teamnet.bootstrap.service.AbstractService;
import ro.teamnet.scheduler.domain.Task;

import java.util.List;


public interface TaskService extends AbstractService<Task, Long> {

    /**
     * Finds all tasks for the given scheduled job id.
     *
     * @param id - the scheduled job id
     * @return all the tasks associated with the scheduled job with the given id
     */
    List<Task> findByScheduledJobId(Long id);
}
