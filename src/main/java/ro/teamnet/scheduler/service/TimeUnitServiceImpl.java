
package ro.teamnet.scheduler.service;


import ro.teamnet.bootstrap.service.AbstractServiceImpl;
import org.springframework.stereotype.Service;

import ro.teamnet.scheduler.domain.TimeUnit;
import ro.teamnet.scheduler.repository.TimeUnitRepository;
import org.springframework.transaction.annotation.Transactional;
import javax.inject.Inject;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TimeUnitServiceImpl extends AbstractServiceImpl<TimeUnit,Long> implements TimeUnitService {

    private final Logger log = LoggerFactory.getLogger(TimeUnitServiceImpl.class);

    @Inject
    private TimeUnitRepository timeUnitRepository;

    @Inject
    public TimeUnitServiceImpl(TimeUnitRepository repository) {
        super(repository);
    }



}
