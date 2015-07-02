package ro.teamnet.scheduler.job;

import org.springframework.stereotype.Service;
import ro.teamnet.scheduler.enums.JobExecutionStatus;
import ro.teamnet.scheduler.service.ExecutionService;

/**
 * Created by Oana.Mihai on 7/2/2015.
 */
@Service
public class MockExecutionService implements ExecutionService {

    @Override
    public Long start(Long configurationId) {
        return configurationId + 1;
    }

    @Override
    public JobExecutionStatus getStatus(Long dataId) {
        return JobExecutionStatus.FINISHED;
    }

    @Override
    public void pause(Long dataId) {

    }

    @Override
    public void resume(Long dataId) {

    }

    @Override
    public void cancel(Long dataId) {

    }

    @Override
    public boolean supports(String delimiter) {
        return delimiter.equalsIgnoreCase("mock");
    }
}
