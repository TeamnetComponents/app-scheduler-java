package ro.teamnet.scheduler.job;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.teamnet.scheduler.enums.JobExecutionStatus;

/**
 * Created by Oana.Mihai on 5/20/2015.
 */
public class MockJob extends AppJob {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public JobExecutionStatus run() {
        log.info("Nothing to execute! Options provided: " + getOptions() + " Fire time: " + new DateTime());
        return JobExecutionStatus.FINISHED;
    }
}
