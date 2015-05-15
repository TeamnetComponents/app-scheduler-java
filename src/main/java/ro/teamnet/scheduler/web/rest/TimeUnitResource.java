package ro.teamnet.scheduler.web.rest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.teamnet.bootstrap.web.rest.AbstractResource;
import ro.teamnet.scheduler.domain.TimeUnit;
import ro.teamnet.scheduler.service.TimeUnitService;

import javax.inject.Inject;

/**
 * REST controller for managing TimeUnit.
 */
@RestController
@RequestMapping("/app/rest/timeUnit")
public class TimeUnitResource extends AbstractResource<TimeUnit, Long> {

    private final Logger log = LoggerFactory.getLogger(TimeUnitResource.class);

    @Inject
    public TimeUnitResource(TimeUnitService service) {
        super(service);
    }


}
