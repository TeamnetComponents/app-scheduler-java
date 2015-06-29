
package ro.teamnet.scheduler.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ro.teamnet.bootstrap.extend.Filter;
import ro.teamnet.bootstrap.extend.Filters;
import ro.teamnet.bootstrap.service.AbstractServiceImpl;
import ro.teamnet.scheduler.domain.ScheduledJob;
import ro.teamnet.scheduler.domain.ScheduledJobExecution;
import ro.teamnet.scheduler.enums.JobExecutionStatus;
import ro.teamnet.scheduler.repository.ScheduledJobExecutionRepository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduledJobExecutionServiceImpl extends AbstractServiceImpl<ScheduledJobExecution, Long> implements ScheduledJobExecutionService {

    private final Logger log = LoggerFactory.getLogger(ScheduledJobExecutionServiceImpl.class);

    @Inject
    private ScheduledJobExecutionRepository scheduledJobExecutionRepository;

    @Inject
    public ScheduledJobExecutionServiceImpl(ScheduledJobExecutionRepository repository) {
        super(repository);
    }


    @Override
    public void updateExecutionStatus(Long executionId, JobExecutionStatus status) {
        if (status == null) {
            return;
        }
        ScheduledJobExecution execution = findOne(executionId);
        if (status.equals(execution.getStatus())) {
            return;
        }
        execution.setStatus(status);
        save(execution);
    }

    @Override
    public void updateExecutionState(Long executionId, String executionState) {
        if (executionState == null) {
            return;
        }
        ScheduledJobExecution execution = findOne(executionId);
        if (executionState.equals(execution.getState())) {
            return;
        }
        execution.setState(executionState);
        save(execution);
    }

    public ScheduledJob findJobByExecutionId(Long id) {
        return scheduledJobExecutionRepository.findJobByExecutionId(id);
    }


    @Override
    public Filters convertDTOFiltersToEntityFilters(Filters dtoFilters) {
        Filters filters = new Filters();
        for (Filter filter : dtoFilters) {
            filters.addFilter(convertFilterToDTOFilter(filter));
        }
        return filters;
    }

    private Filter convertFilterToDTOFilter(Filter filter) {
        if (filter.getProperty().equals("previousFireTime")) {
            filter.setProperty("lastFireTime");
        } else if (filter.getProperty().equals("executionDetails")) {
            filter.setProperty("state");
        }
        return filter;
    }

    @Override
    public Sort convertDTOSortToEntitySort(Sort dtoSort) {
        List<Sort.Order> orders = new ArrayList<>();
        for (Sort.Order order : dtoSort) {
            orders.add(convertOrderToDTOOrder(order));
        }
        return new Sort(orders);
    }

    private Sort.Order convertOrderToDTOOrder(Sort.Order order) {
        Sort.Order newOrder = order;

        if (order.getProperty().equals("previousFireTime")) {
            newOrder = new Sort.Order(order.getDirection(), "lastFireTime");
        } else if (order.getProperty().equals("executionDetails")) {
            newOrder = new Sort.Order(order.getDirection(), "state");
        }
        return newOrder;
    }
}
