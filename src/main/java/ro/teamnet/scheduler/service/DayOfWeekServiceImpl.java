package ro.teamnet.scheduler.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ro.teamnet.bootstrap.service.AbstractServiceImpl;
import ro.teamnet.scheduler.domain.DayOfWeek;
import ro.teamnet.scheduler.repository.DayOfWeekRepository;

import javax.inject.Inject;

@Service
public class DayOfWeekServiceImpl extends AbstractServiceImpl<DayOfWeek, Long> implements DayOfWeekService {

    private final Logger log = LoggerFactory.getLogger(DayOfWeekServiceImpl.class);

    @Inject
    private DayOfWeekRepository dayOfWeekRepository;

    @Inject
    public DayOfWeekServiceImpl(DayOfWeekRepository repository) {
        super(repository);
    }


}
