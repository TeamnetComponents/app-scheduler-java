package ro.teamnet.scheduler.web.rest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.teamnet.bootstrap.web.rest.AbstractResource;
import ro.teamnet.scheduler.domain.Month;
import ro.teamnet.scheduler.service.MonthService;

import javax.inject.Inject;

/**
 * REST controller for managing Month.
 */
@RestController
@RequestMapping("/app/rest/month")
public class MonthResource extends AbstractResource<Month, Long> {

    private final Logger log = LoggerFactory.getLogger(MonthResource.class);

    @Inject
    public MonthResource(MonthService service) {
        super(service);
    }


}