
package ro.teamnet.scheduler.web.rest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.teamnet.bootstrap.web.rest.AbstractResource;
import ro.teamnet.scheduler.domain.Schedule;
import ro.teamnet.scheduler.service.ScheduleService;

import javax.inject.Inject;

/**
 * REST controller for managing Schedule.
 */
@RestController
@RequestMapping("/app/rest/schedule")
public class ScheduleResource extends AbstractResource<Schedule,Long>{

    private final Logger log = LoggerFactory.getLogger(ScheduleResource.class);

    @Inject
    public ScheduleResource(ScheduleService service) {
        super(service);
    }
}
