package ro.teamnet.scheduler.web.rest;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.teamnet.bootstrap.web.rest.AbstractResource;
import ro.teamnet.scheduler.domain.RecurrentTimeUnit;
import ro.teamnet.scheduler.service.RecurrentTimeUnitService;

import javax.inject.Inject;

/**
 * REST controller for managing RecurrentTimeUnit.
 */
@RestController
@RequestMapping("/app/rest/recurrentTimeUnit")
public class RecurrentTimeUnitResource extends AbstractResource<RecurrentTimeUnit, Long> {

    @Inject
    public RecurrentTimeUnitResource(RecurrentTimeUnitService service) {
        super(service);
    }


}
