package ro.teamnet.scheduler.service;


import ro.teamnet.bootstrap.service.AbstractService;
import ro.teamnet.scheduler.domain.Schedule;

import java.util.List;


public interface ScheduleService extends AbstractService<Schedule, Long> {

    /**
     * Finds all schedules for the given job id.
     *
     * @param scheduledJobId - the scheduled job id
     * @return all the schedules associated with the job with the given id
     */
    List<Schedule> findByScheduledJobId(Long scheduledJobId);
}
