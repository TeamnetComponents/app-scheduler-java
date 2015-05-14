
package ro.teamnet.scheduler.web.rest;


import ro.teamnet.bootstrap.web.rest.AbstractResource;
import com.codahale.metrics.annotation.Timed;

import ro.teamnet.scheduler.domain.RecurrentYear;
import ro.teamnet.scheduler.service.RecurrentYearService;
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
 * REST controller for managing RecurrentYear.
 */
@RestController
@RequestMapping("/app/rest/recurrentYear")
public class RecurrentYearResource extends AbstractResource<RecurrentYear,Long>{

    private final Logger log = LoggerFactory.getLogger(RecurrentYearResource.class);

    @Inject
    public RecurrentYearResource(RecurrentYearService service) {
            super(service);
       }


}
