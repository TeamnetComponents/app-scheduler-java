package ro.teamnet.scheduler.web.rest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.teamnet.bootstrap.web.rest.AbstractResource;
import ro.teamnet.scheduler.domain.DayOfWeek;
import ro.teamnet.scheduler.service.DayOfWeekService;

import javax.inject.Inject;

/**
 * REST controller for managing DayOfWeek.
 */
@RestController
@RequestMapping("/app/rest/dayOfWeek")
public class DayOfWeekResource extends AbstractResource<DayOfWeek, Long> {

    private final Logger log = LoggerFactory.getLogger(DayOfWeekResource.class);

    @Inject
    public DayOfWeekResource(DayOfWeekService service) {
        super(service);
    }


}
