
package ro.teamnet.scheduler.web.rest;


import org.springframework.beans.factory.annotation.Autowired;
import ro.teamnet.bootstrap.web.rest.AbstractResource;
import com.codahale.metrics.annotation.Timed;

import ro.teamnet.scheduler.domain.ScheduledJob;
import ro.teamnet.scheduler.domain.ScheduledJobExecution;
import ro.teamnet.scheduler.service.ScheduledJobExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * REST controller for managing ScheduledJobExecution.
 */
@RestController
@RequestMapping("/app/rest/scheduledJobExecution")
public class ScheduledJobExecutionResource extends AbstractResource<ScheduledJobExecution,Long>{

    private final Logger log = LoggerFactory.getLogger(ScheduledJobExecutionResource.class);

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
