package ro.teamnet.scheduler.web.rest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.teamnet.bootstrap.web.rest.AbstractResource;
import ro.teamnet.scheduler.domain.Task;
import ro.teamnet.scheduler.service.TaskService;

import javax.inject.Inject;

/**
 * REST controller for managing Task.
 */
@RestController
@RequestMapping("/app/rest/task")
public class TaskResource extends AbstractResource<Task, Long> {

    private final Logger log = LoggerFactory.getLogger(TaskResource.class);

    @Inject
    public TaskResource(TaskService service) {
        super(service);
    }
}
