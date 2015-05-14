
package ro.teamnet.scheduler.service;


import ro.teamnet.bootstrap.service.AbstractServiceImpl;
import org.springframework.stereotype.Service;

import ro.teamnet.scheduler.domain.DayOfWeek;
import ro.teamnet.scheduler.repository.DayOfWeekRepository;
import org.springframework.transaction.annotation.Transactional;
import javax.inject.Inject;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DayOfWeekServiceImpl extends AbstractServiceImpl<DayOfWeek,Long> implements DayOfWeekService {

    private final Logger log = LoggerFactory.getLogger(DayOfWeekServiceImpl.class);

    @Inject
    private DayOfWeekRepository dayOfWeekRepository;

    @Inject
    public DayOfWeekServiceImpl(DayOfWeekRepository repository) {
        super(repository);
    }



}
