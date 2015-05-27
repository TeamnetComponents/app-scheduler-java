package ro.teamnet.scheduler.service;


import ro.teamnet.bootstrap.service.AbstractService;
import ro.teamnet.scheduler.domain.Task;

import java.util.List;
import java.util.Map;


public interface TaskService extends AbstractService<Task, Long> {

    /**
     * Finds all tasks for the given scheduled job id.
     *
     * @param id - the scheduled job id
     * @return all the tasks associated with the scheduled job with the given id
     */
    List<Task> findByScheduledJobId(Long id);

    /**
     * Extracts all task options associated with the given scheduled job id, mapped with the position the task holds in
     * the execution queue.
     *
     * @param scheduledJobId id of the scheduled job
     * @return a map of task options: the key represents the task position in the execution queue
     */
    Map<Integer, String> getTaskOptionsByQueuePosition(Long scheduledJobId);
}
