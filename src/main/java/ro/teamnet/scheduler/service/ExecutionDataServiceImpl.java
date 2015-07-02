
package ro.teamnet.scheduler.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ro.teamnet.bootstrap.service.AbstractServiceImpl;
import ro.teamnet.scheduler.domain.ExecutionData;
import ro.teamnet.scheduler.repository.ExecutionDataRepository;

import javax.inject.Inject;

@Service
public class ExecutionDataServiceImpl extends AbstractServiceImpl<ExecutionData, Long> implements ExecutionDataService {

    private final Logger log = LoggerFactory.getLogger(ExecutionDataServiceImpl.class);


    @Inject
    public ExecutionDataServiceImpl(ExecutionDataRepository repository) {
        super(repository);
    }

    private ExecutionDataRepository getExecutionDataRepository() {
        return (ExecutionDataRepository) getRepository();
    }

    @Override
    public ExecutionData findByConfigurationIdAndTypeAndExecutionId(Long configurationId, String configurationType,
                                                                    Long executionId) {
        return getExecutionDataRepository().findByConfigurationIdAndTypeAndExecutionId(configurationId,
                configurationType, executionId);
    }
}
