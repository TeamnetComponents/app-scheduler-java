package ro.teamnet.scheduler.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
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
    private ScheduledJobRepository scheduledJobRepository;

    @Inject
    public ScheduledJobServiceImpl(ScheduledJobRepository repository) {
        super(repository);
    }

    /**
     * Logical delete action.
     *
     * @param id - the element to delete
     */
    @Override
    public void delete(Long id) {
        ScheduledJob scheduledJob = findOne(id);
        scheduledJob.setDeleted(true);
        save(scheduledJob);
    }

    @Override
    public List<ScheduledJob> findAll() {
        return scheduledJobRepository.findByDeletedFalse();
    }

    @Override
    public AppPage<ScheduledJob> findAll(AppPageable appPageable) {
        appPageable.getFilters().addFilter(new Filter("deleted", Boolean.FALSE.toString(), Filter.FilterType.EQUAL));
        return super.findAll(appPageable);
    }
}
