package ro.teamnet.scheduler.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ro.teamnet.bootstrap.service.AbstractServiceImpl;
import ro.teamnet.scheduler.domain.TimeUnit;
import ro.teamnet.scheduler.repository.TimeUnitRepository;

import javax.inject.Inject;

@Service
public class TimeUnitServiceImpl extends AbstractServiceImpl<TimeUnit, Long> implements TimeUnitService {

    private final Logger log = LoggerFactory.getLogger(TimeUnitServiceImpl.class);

    @Inject
    private TimeUnitRepository timeUnitRepository;

    @Inject
    public TimeUnitServiceImpl(TimeUnitRepository repository) {
        super(repository);
    }


}
