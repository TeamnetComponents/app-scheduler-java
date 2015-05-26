package ro.teamnet.scheduler.service;


import ro.teamnet.bootstrap.service.AbstractService;
import ro.teamnet.scheduler.domain.TimeUnit;

import java.util.List;


public interface TimeUnitService extends AbstractService<TimeUnit, Long> {

    TimeUnit findByCode(String code);
}
