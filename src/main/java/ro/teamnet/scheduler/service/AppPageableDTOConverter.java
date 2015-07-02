package ro.teamnet.scheduler.service;

import org.springframework.data.domain.Sort;
import ro.teamnet.bootstrap.extend.AppPageRequest;
import ro.teamnet.bootstrap.extend.AppPageable;
import ro.teamnet.bootstrap.extend.Filter;
import ro.teamnet.bootstrap.extend.Filters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppPageableDTOConverter {
    private final Map<String, String> dtoEntityPropertyMap;
    private final AppPageable dtoPageable;
    private final Sort entitySort;
    private final Filters entityFilters;

    public AppPageableDTOConverter(Map<String, String> dtoEntityPropertyMap, AppPageable dtoPageable) {
        this.dtoEntityPropertyMap = dtoEntityPropertyMap;
        this.dtoPageable = dtoPageable;
        entitySort = convertDTOSortToEntitySort(dtoPageable.getSort());
        entityFilters = convertDTOFiltersToEntityFilters(dtoPageable.getFilters());
    }

    public AppPageableDTOConverter addFilter(Filter filter) {
        entityFilters.addFilter(filter);
        return this;
    }

    public AppPageable getEntityPageable() {
        return new AppPageRequest(dtoPageable.getPageNumber(), dtoPageable.getPageSize(),
                entitySort, entityFilters, dtoPageable.locale());
    }

    private Filters convertDTOFiltersToEntityFilters(Filters dtoFilters) {
        Filters filters = new Filters();
        for (Filter filter : dtoFilters) {
            filters.addFilter(convertDTOFilterToEntityFilter(filter));
        }
        return filters;
    }

    private Filter convertDTOFilterToEntityFilter(Filter filter) {
        String entityProperty = dtoEntityPropertyMap.get(filter.getProperty());
        if (entityProperty != null) {
            filter.setProperty(entityProperty);
        }
        return filter;
    }

    private Sort convertDTOSortToEntitySort(Sort dtoSort) {
        if (dtoSort == null) {
            return null;
        }
        List<Sort.Order> orders = new ArrayList<>();
        for (Sort.Order order : dtoSort) {
            orders.add(convertDTOOrderToEntityOrder(order));
        }
        return new Sort(orders);
    }

    private Sort.Order convertDTOOrderToEntityOrder(Sort.Order order) {
        String entityProperty = dtoEntityPropertyMap.get(order.getProperty());
        if (entityProperty != null) {
            return new Sort.Order(order.getDirection(), entityProperty);
        }
        return order;
    }
}