
package ro.teamnet.scheduler.service;


import org.springframework.stereotype.Service;
import ro.teamnet.bootstrap.service.AbstractServiceImpl;
import ro.teamnet.scheduler.domain.ExecutionData;
import ro.teamnet.scheduler.repository.ExecutionDataRepository;

import javax.inject.Inject;

@Service
public class ExecutionDataServiceImpl extends AbstractServiceImpl<ExecutionData, Long> implements ExecutionDataService {

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
