package ro.teamnet.scheduler.web.rest;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.teamnet.bootstrap.web.rest.AbstractResource;
import ro.teamnet.scheduler.domain.ScheduledJob;
import ro.teamnet.scheduler.service.ScheduledJobService;

import javax.inject.Inject;

/**
 * REST controller for managing ScheduledJob.
 */
@RestController
@RequestMapping("/app/rest/scheduledJob")
public class ScheduledJobResource extends AbstractResource<ScheduledJob, Long> {

    @Inject
    public ScheduledJobResource(ScheduledJobService service) {
        super(service);
    }
}
