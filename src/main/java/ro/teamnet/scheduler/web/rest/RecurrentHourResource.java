
package ro.teamnet.scheduler.web.rest;


import ro.teamnet.bootstrap.web.rest.AbstractResource;
import com.codahale.metrics.annotation.Timed;

import ro.teamnet.scheduler.domain.RecurrentHour;
import ro.teamnet.scheduler.service.RecurrentHourService;
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
 * REST controller for managing RecurrentHour.
 */
@RestController
@RequestMapping("/app/rest/recurrentHour")
public class RecurrentHourResource extends AbstractResource<RecurrentHour,Long>{

    private final Logger log = LoggerFactory.getLogger(RecurrentHourResource.class);

    @Inject
    public RecurrentHourResource(RecurrentHourService service) {
            super(service);
       }


}
