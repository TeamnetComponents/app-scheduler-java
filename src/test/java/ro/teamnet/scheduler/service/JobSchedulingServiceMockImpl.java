package ro.teamnet.scheduler.service;

import org.springframework.stereotype.Service;
import ro.teamnet.scheduler.domain.Schedule;
import ro.teamnet.scheduler.domain.ScheduledJob;

/**
 * Created by Oana.Mihai on 6/16/2015.
 */
@Service
public class JobSchedulingServiceMockImpl implements JobSchedulingService {


    @Override
    public void onScheduledJobSave(ScheduledJob job) {

    }

    @Override
    public void onScheduledJobDelete(Long jobId) {

    }

    @Override
    public void onScheduleSave(Schedule schedule) {

    }

    @Override
    public void onScheduleDelete(Long scheduleId) {

    }
}
