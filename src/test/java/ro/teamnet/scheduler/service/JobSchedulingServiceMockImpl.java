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
    public void createJob(ScheduledJob job) {

    }

    @Override
    public void updateJob(ScheduledJob job) {

    }

    @Override
    public void deleteJob(ScheduledJob job) {

    }

    @Override
    public void addTrigger(Schedule schedule) {

    }

    @Override
    public void deleteTrigger(Schedule schedule) {

    }
}
