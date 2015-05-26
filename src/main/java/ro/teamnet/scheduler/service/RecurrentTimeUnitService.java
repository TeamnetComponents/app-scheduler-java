package ro.teamnet.scheduler.service;


import ro.teamnet.bootstrap.service.AbstractService;
import ro.teamnet.scheduler.domain.RecurrentTimeUnit;
import ro.teamnet.scheduler.domain.TimeUnit;

import java.util.List;


public interface RecurrentTimeUnitService extends AbstractService<RecurrentTimeUnit, Long> {

    RecurrentTimeUnit findByIdAndTimeUnit(Long id, TimeUnit timeUnit);
}
