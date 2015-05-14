
package ro.teamnet.scheduler.web.rest;


import ro.teamnet.bootstrap.web.rest.AbstractResource;
import com.codahale.metrics.annotation.Timed;

import ro.teamnet.scheduler.domain.Month;
import ro.teamnet.scheduler.service.MonthService;
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
 * REST controller for managing Month.
 */
@RestController
@RequestMapping("/app/rest/month")
public class MonthResource extends AbstractResource<Month,Long>{

    private final Logger log = LoggerFactory.getLogger(MonthResource.class);

    @Inject
    public MonthResource(MonthService service) {
            super(service);
       }


}
