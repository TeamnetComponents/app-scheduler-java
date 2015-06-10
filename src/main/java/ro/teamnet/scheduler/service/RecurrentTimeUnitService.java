package ro.teamnet.scheduler.service;


import ro.teamnet.bootstrap.service.AbstractService;
import ro.teamnet.scheduler.domain.RecurrentTimeUnit;

import java.util.Set;


public interface RecurrentTimeUnitService extends AbstractService<RecurrentTimeUnit, Long> {

    RecurrentTimeUnit findByCodeOfTimeUnitAndValue(String code, Integer value);

    Set<RecurrentTimeUnit> findByScheduleId(Long id);

}
