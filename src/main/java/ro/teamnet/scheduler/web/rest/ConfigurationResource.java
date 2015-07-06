
package ro.teamnet.scheduler.web.rest;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.teamnet.bootstrap.web.rest.AbstractResource;
import ro.teamnet.scheduler.domain.Configuration;
import ro.teamnet.scheduler.service.ConfigurationService;

import javax.inject.Inject;

/**
 * REST controller for managing Configuration.
 */
@RestController
@RequestMapping("/app/rest/configuration")
public class ConfigurationResource extends AbstractResource<Configuration,Long>{

    @Inject
    public ConfigurationResource(ConfigurationService service) {
        super(service);
    }
}
