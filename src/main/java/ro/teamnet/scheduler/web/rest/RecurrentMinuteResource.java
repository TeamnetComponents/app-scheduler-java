
package ro.teamnet.scheduler.web.rest;


import ro.teamnet.bootstrap.web.rest.AbstractResource;
import com.codahale.metrics.annotation.Timed;

import ro.teamnet.scheduler.domain.RecurrentMinute;
import ro.teamnet.scheduler.service.RecurrentMinuteService;
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
 * REST controller for managing RecurrentMinute.
 */
@RestController
@RequestMapping("/app/rest/recurrentMinute")
public class RecurrentMinuteResource extends AbstractResource<RecurrentMinute,Long>{

    private final Logger log = LoggerFactory.getLogger(RecurrentMinuteResource.class);

    @Inject
    public RecurrentMinuteResource(RecurrentMinuteService service) {
            super(service);
       }


}
