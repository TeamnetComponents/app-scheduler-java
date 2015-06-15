package ro.teamnet.scheduler.service;

import ro.teamnet.scheduler.domain.Schedule;
import ro.teamnet.scheduler.domain.ScheduledJob;

/**
 * Created by Oana.Mihai on 6/15/2015.
 */
public interface JobSchedulingService {
    void createJob(ScheduledJob job);
    void updateJob(ScheduledJob job);
    void deleteJob(ScheduledJob job);
    void addTrigger(Schedule schedule);
    void deleteTrigger(Schedule schedule);
}
