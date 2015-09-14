package ro.teamnet.scheduler.service;


import ro.teamnet.bootstrap.service.AbstractService;
import ro.teamnet.scheduler.domain.TimeUnit;
import ro.teamnet.scheduler.enums.TimeUnitCode;


public interface TimeUnitService extends AbstractService<TimeUnit, Long> {

    TimeUnit findByCode(TimeUnitCode code);

}
