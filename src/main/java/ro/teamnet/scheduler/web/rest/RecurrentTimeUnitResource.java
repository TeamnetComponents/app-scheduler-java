
package ro.teamnet.scheduler.web.rest;


import ro.teamnet.bootstrap.web.rest.AbstractResource;
import com.codahale.metrics.annotation.Timed;

import ro.teamnet.scheduler.domain.RecurrentTimeUnit;
import ro.teamnet.scheduler.service.RecurrentTimeUnitService;
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
 * REST controller for managing RecurrentTimeUnit.
 */
@RestController
@RequestMapping("/app/rest/recurrentTimeUnit")
public class RecurrentTimeUnitResource extends AbstractResource<RecurrentTimeUnit,Long>{

    private final Logger log = LoggerFactory.getLogger(RecurrentTimeUnitResource.class);

    @Inject
    public RecurrentTimeUnitResource(RecurrentTimeUnitService service) {
            super(service);
       }


}
