
package ro.teamnet.scheduler.web.rest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ro.teamnet.bootstrap.web.rest.AbstractResource;
import ro.teamnet.scheduler.domain.ScheduledJob;
import ro.teamnet.scheduler.domain.ScheduledJobExecution;
import ro.teamnet.scheduler.service.ScheduledJobExecutionService;

import javax.inject.Inject;

/**
 * REST controller for managing ScheduledJobExecution.
 */
@RestController
@RequestMapping("/app/rest/scheduledJobExecution")
public class ScheduledJobExecutionResource extends AbstractResource<ScheduledJobExecution,Long>{

    @Inject
    public ScheduledJobExecutionResource(ScheduledJobExecutionService service) {
        super(service);
    }

    @Autowired
    ScheduledJobExecutionService service;

    @RequestMapping(value="/getjob/{id}", method=RequestMethod.GET)
    public ScheduledJob findJobByExecutionId(@PathVariable Long id) {
        return service.findJobByExecutionId(id);
    }
}
