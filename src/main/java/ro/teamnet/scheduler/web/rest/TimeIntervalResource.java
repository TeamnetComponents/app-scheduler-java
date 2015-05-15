package ro.teamnet.scheduler.web.rest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.teamnet.bootstrap.web.rest.AbstractResource;
import ro.teamnet.scheduler.domain.TimeInterval;
import ro.teamnet.scheduler.service.TimeIntervalService;

import javax.inject.Inject;

/**
 * REST controller for managing TimeInterval.
 */
@RestController
@RequestMapping("/app/rest/timeInterval")
public class TimeIntervalResource extends AbstractResource<TimeInterval, Long> {

    private final Logger log = LoggerFactory.getLogger(TimeIntervalResource.class);

    @Inject
    public TimeIntervalResource(TimeIntervalService service) {
        super(service);
    }


}
