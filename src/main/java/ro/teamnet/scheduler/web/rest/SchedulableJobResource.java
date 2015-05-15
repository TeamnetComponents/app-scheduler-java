package ro.teamnet.scheduler.web.rest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.teamnet.bootstrap.web.rest.AbstractResource;
import ro.teamnet.scheduler.domain.SchedulableJob;
import ro.teamnet.scheduler.service.SchedulableJobService;

import javax.inject.Inject;

/**
 * REST controller for managing SchedulableJob.
 */
@RestController
@RequestMapping("/app/rest/schedulableJob")
public class SchedulableJobResource extends AbstractResource<SchedulableJob, Long> {

    private final Logger log = LoggerFactory.getLogger(SchedulableJobResource.class);

    @Inject
    public SchedulableJobResource(SchedulableJobService service) {
        super(service);
    }
}
