
package ro.teamnet.scheduler.web.rest;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.teamnet.bootstrap.web.rest.AbstractResource;
import ro.teamnet.scheduler.domain.ExecutionData;
import ro.teamnet.scheduler.service.ExecutionDataService;

import javax.inject.Inject;

/**
 * REST controller for managing ExecutionData.
 */
@RestController
@RequestMapping("/app/rest/executionData")
public class ExecutionDataResource extends AbstractResource<ExecutionData,Long>{

    @Inject
    public ExecutionDataResource(ExecutionDataService service) {
        super(service);
    }
}
