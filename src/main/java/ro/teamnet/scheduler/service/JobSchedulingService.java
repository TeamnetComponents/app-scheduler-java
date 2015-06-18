package ro.teamnet.scheduler.service;

import ro.teamnet.scheduler.domain.Schedule;
import ro.teamnet.scheduler.domain.ScheduledJob;
import ro.teamnet.scheduler.domain.Task;

/**
 * Created by Oana.Mihai on 6/15/2015.
 */
public interface JobSchedulingService {
    void onScheduledJobSave(ScheduledJob job);
    void onScheduledJobDelete(Long jobId);
    void onScheduleSave(Schedule schedule);
    void onScheduleDelete(Long scheduleId);
    void onTaskSave(Task task);
    void onTaskDelete(Long taskId);
}
