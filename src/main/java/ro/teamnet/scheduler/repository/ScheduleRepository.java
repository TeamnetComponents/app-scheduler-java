package ro.teamnet.scheduler.repository;

import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ro.teamnet.bootstrap.extend.AppRepository;
import ro.teamnet.scheduler.domain.Schedule;

import java.util.List;

/**
 * Spring Data JPA repository for the Schedule entity.
 */
public interface ScheduleRepository extends AppRepository<Schedule, Long> {


    @Override
    @Query("select schedule from Schedule schedule left join fetch schedule.timeInterval left join fetch schedule.scheduledJob where schedule.id =:id")
    Schedule findOne(@Param("id") Long id);

    /**
     * Finds all schedules for the given job id (skips deleted schedules).
     *
     * @param scheduledJobId - the scheduled job id
     * @return all the schedules associated with the job with the given id
     */
    List<Schedule> findByDeletedFalseAndScheduledJobId(Long scheduledJobId);

    /**
     * Finds all schedules (skips deleted).
     * @return all schedules (minus the ones marked as deleted)
     */
    List<Schedule> findByDeletedFalse();

    Schedule findByStartTime(DateTime startTime);
}
