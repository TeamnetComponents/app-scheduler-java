
package ro.teamnet.scheduler.web.rest;


import ro.teamnet.bootstrap.web.rest.AbstractResource;
import com.codahale.metrics.annotation.Timed;

import ro.teamnet.scheduler.domain.RecurrentDay;
import ro.teamnet.scheduler.service.RecurrentDayService;
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
 * REST controller for managing RecurrentDay.
 */
@RestController
@RequestMapping("/app/rest/recurrentDay")
public class RecurrentDayResource extends AbstractResource<RecurrentDay,Long>{

    private final Logger log = LoggerFactory.getLogger(RecurrentDayResource.class);

    @Inject
    public RecurrentDayResource(RecurrentDayService service) {
            super(service);
       }


}
