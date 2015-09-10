package ro.teamnet.scheduler.service;


import ro.teamnet.bootstrap.service.AbstractService;
import ro.teamnet.scheduler.domain.ScheduledJob;

import java.util.List;


public interface ScheduledJobService extends AbstractService<ScheduledJob, Long> {

    List<ScheduledJob> findAllWithDeleted();

    void importJobTriggers();
}
