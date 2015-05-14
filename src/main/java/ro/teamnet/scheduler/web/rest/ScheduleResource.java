
package ro.teamnet.scheduler.web.rest;


import ro.teamnet.bootstrap.web.rest.AbstractResource;
import com.codahale.metrics.annotation.Timed;

import ro.teamnet.scheduler.domain.Schedule;
import ro.teamnet.scheduler.service.ScheduleService;
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
