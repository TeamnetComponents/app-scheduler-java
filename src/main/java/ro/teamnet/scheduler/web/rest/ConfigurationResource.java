
package ro.teamnet.scheduler.web.rest;


import ro.teamnet.bootstrap.web.rest.AbstractResource;
import com.codahale.metrics.annotation.Timed;

import ro.teamnet.scheduler.domain.Configuration;
import ro.teamnet.scheduler.service.ConfigurationService;
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
 * REST controller for managing Configuration.
 */
@RestController
@RequestMapping("/app/rest/configuration")
public class ConfigurationResource extends AbstractResource<Configuration,Long>{

    private final Logger log = LoggerFactory.getLogger(ConfigurationResource.class);

    @Inject
    public ConfigurationResource(ConfigurationService service) {
        super(service);
    }
}
