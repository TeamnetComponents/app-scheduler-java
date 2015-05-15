package ro.teamnet.scheduler.domain;

import org.quartz.Job;

/**
 * The job interface.
 */
public interface AppJob extends Job {

    void configure(String optionValues);
}
