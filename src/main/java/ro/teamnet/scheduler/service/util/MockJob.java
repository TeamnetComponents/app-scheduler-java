package ro.teamnet.scheduler.service.util;

import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Oana.Mihai on 5/20/2015.
 */
public class MockJob implements Job{

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Nothing to execute! Options provided: " + context.getMergedJobDataMap().getString("options") + " Fire time: " + new DateTime());
    }
}
