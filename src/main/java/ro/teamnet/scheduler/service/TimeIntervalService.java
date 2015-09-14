package ro.teamnet.scheduler.service;


import ro.teamnet.bootstrap.service.AbstractService;
import ro.teamnet.scheduler.domain.TimeInterval;
import ro.teamnet.scheduler.dto.TimeIntervalDTO;


public interface TimeIntervalService extends AbstractService<TimeInterval, Long> {

    TimeInterval findOneOrCreate(TimeIntervalDTO timeIntervalDTO);
}
