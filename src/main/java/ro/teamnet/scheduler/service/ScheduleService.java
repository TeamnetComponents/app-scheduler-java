package ro.teamnet.scheduler.service;


import org.joda.time.DateTime;
import ro.teamnet.bootstrap.service.AbstractService;
import ro.teamnet.scheduler.domain.Schedule;
import ro.teamnet.scheduler.domain.TimeInterval;

import java.util.List;


public interface ScheduleService extends AbstractService<Schedule, Long> {

    /**
     * Finds all schedules for the given job id.
     *
     * @param scheduledJobId - the scheduled job id
     * @return all the schedules associated with the job with the given id
     */
    List<Schedule> findByScheduledJobId(Long scheduledJobId);

    Schedule findByStartDate(DateTime startDate);
    Schedule findByStartTimeAndRecurrent(DateTime startTime, Boolean recurrent);
    Schedule findByStartTimeAndRecurrentAndTimeInterval(DateTime startTime, Boolean recurrent, TimeInterval timeInterval);
    Schedule findByIdAndStartTimeAndRecurrent(Long id, DateTime startTime, Boolean recurrent);
    Schedule findByStartTimeAndRecurrentAndLastUpdated(DateTime startTime, Boolean recurrent, DateTime lastUpdated);
}
