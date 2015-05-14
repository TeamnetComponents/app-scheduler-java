
package ro.teamnet.scheduler.web.rest;


import ro.teamnet.bootstrap.web.rest.AbstractResource;
import com.codahale.metrics.annotation.Timed;

import ro.teamnet.scheduler.domain.DayOfWeek;
import ro.teamnet.scheduler.service.DayOfWeekService;
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
 * REST controller for managing DayOfWeek.
 */
@RestController
@RequestMapping("/app/rest/dayOfWeek")
public class DayOfWeekResource extends AbstractResource<DayOfWeek,Long>{

    private final Logger log = LoggerFactory.getLogger(DayOfWeekResource.class);

    @Inject
    public DayOfWeekResource(DayOfWeekService service) {
            super(service);
       }


}
