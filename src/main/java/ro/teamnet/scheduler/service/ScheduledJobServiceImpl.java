package ro.teamnet.scheduler.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.teamnet.bootstrap.extend.AppPage;
import ro.teamnet.bootstrap.extend.AppPageable;
import ro.teamnet.bootstrap.extend.Filter;
import ro.teamnet.bootstrap.service.AbstractServiceImpl;
import ro.teamnet.scheduler.domain.ScheduledJob;
import ro.teamnet.scheduler.repository.ScheduledJobRepository;

import javax.inject.Inject;
import java.util.List;

@Service
public class ScheduledJobServiceImpl extends AbstractServiceImpl<ScheduledJob, Long> implements ScheduledJobService {

    private final Logger log = LoggerFactory.getLogger(ScheduledJobServiceImpl.class);

    @Inject
    public ScheduledJobServiceImpl(ScheduledJobRepository repository) {
        super(repository);
    }

    private ScheduledJobRepository getScheduledJobRepository() {
        return (ScheduledJobRepository) getRepository();
    }

    /**
     * Logical delete action.
     *
     * @param id - the element to delete
     */
    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Performing logical delete of scheduled job with id: " + id);
        ScheduledJob scheduledJob = findOne(id);
        scheduledJob.setDeleted(true);
        save(scheduledJob);
    }

    @Override
    public List<ScheduledJob> findAll() {
        return getScheduledJobRepository().findByDeletedFalse();
    }

    @Override
    public AppPage<ScheduledJob> findAll(AppPageable appPageable) {
        appPageable.getFilters().addFilter(new Filter("deleted", Boolean.FALSE.toString(), Filter.FilterType.EQUAL));
        return super.findAll(appPageable);
    }

    @Override
    public List<ScheduledJob> findAllWithDeleted() {
        return getScheduledJobRepository().findAll();
    }

    @Override
    @Transactional
    public ScheduledJob save(ScheduledJob scheduledJob) {
        log.info("Saving scheduled job");
        return super.save(scheduledJob);
    }

    @Inject
    JobSchedulingService jobSchedulingService;

    @Scheduled(cron="0 39 20 5 8 ?")
    @Transactional
    public void importJobTriggers() {
        List<ScheduledJob> scheduledJobList = findAll();
        for (ScheduledJob scheduledJob : scheduledJobList) {
            jobSchedulingService.onScheduledJobSave(scheduledJob);
        }
    }
}
