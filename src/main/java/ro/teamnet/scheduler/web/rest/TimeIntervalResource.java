
package ro.teamnet.scheduler.web.rest;


import ro.teamnet.bootstrap.web.rest.AbstractResource;
import com.codahale.metrics.annotation.Timed;

import ro.teamnet.scheduler.domain.TimeInterval;
import ro.teamnet.scheduler.service.TimeIntervalService;
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
 * REST controller for managing TimeInterval.
 */
@RestController
@RequestMapping("/app/rest/timeInterval")
public class TimeIntervalResource extends AbstractResource<TimeInterval,Long>{

    private final Logger log = LoggerFactory.getLogger(TimeIntervalResource.class);

    @Inject
    public TimeIntervalResource(TimeIntervalService service) {
            super(service);
       }


}
