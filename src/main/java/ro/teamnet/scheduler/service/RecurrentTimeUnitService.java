package ro.teamnet.scheduler.service;


import ro.teamnet.bootstrap.service.AbstractService;
import ro.teamnet.scheduler.domain.RecurrentTimeUnit;


public interface RecurrentTimeUnitService extends AbstractService<RecurrentTimeUnit, Long> {

    Long deleteByScheduleId(Long id);

}
