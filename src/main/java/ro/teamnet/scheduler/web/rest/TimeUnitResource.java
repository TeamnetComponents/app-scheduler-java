
package ro.teamnet.scheduler.web.rest;


import ro.teamnet.bootstrap.web.rest.AbstractResource;
import com.codahale.metrics.annotation.Timed;

import ro.teamnet.scheduler.domain.TimeUnit;
import ro.teamnet.scheduler.service.TimeUnitService;
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
 * REST controller for managing TimeUnit.
 */
@RestController
@RequestMapping("/app/rest/timeUnit")
public class TimeUnitResource extends AbstractResource<TimeUnit,Long>{

    private final Logger log = LoggerFactory.getLogger(TimeUnitResource.class);

    @Inject
    public TimeUnitResource(TimeUnitService service) {
            super(service);
       }


}
