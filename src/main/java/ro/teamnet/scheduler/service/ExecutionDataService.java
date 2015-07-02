
package ro.teamnet.scheduler.service;


import ro.teamnet.bootstrap.service.AbstractService;
import ro.teamnet.scheduler.domain.ExecutionData;


public interface ExecutionDataService extends AbstractService<ExecutionData, Long> {

    ExecutionData findByConfigurationIdAndTypeAndExecutionId(Long configurationId, String configurationType,
                                                             Long executionId);

}
