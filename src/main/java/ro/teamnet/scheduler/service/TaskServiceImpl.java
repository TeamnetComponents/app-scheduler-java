package ro.teamnet.scheduler.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ro.teamnet.bootstrap.service.AbstractServiceImpl;
import ro.teamnet.scheduler.domain.Task;
import ro.teamnet.scheduler.repository.TaskRepository;

import javax.inject.Inject;
import java.util.List;

@Service
public class TaskServiceImpl extends AbstractServiceImpl<Task, Long> implements TaskService {

    private final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Inject
    private TaskRepository taskRepository;

    @Inject
    public TaskServiceImpl(TaskRepository repository) {
        super(repository);
    }

    @Override
    public List<Task> findByScheduledJobId(Long id) {
        return taskRepository.findByScheduledJobId(id);
    }

    /**
     * Logical delete action.
     *
     * @param id - the element to delete
     */
    @Override
    public void delete(Long id) {
        Task task = findOne(id);
        task.setDeleted(true);
        save(task);
    }
}
