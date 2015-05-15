package ro.teamnet.scheduler.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ro.teamnet.bootstrap.service.AbstractServiceImpl;
import ro.teamnet.scheduler.domain.Month;
import ro.teamnet.scheduler.repository.MonthRepository;

import javax.inject.Inject;

@Service
public class MonthServiceImpl extends AbstractServiceImpl<Month, Long> implements MonthService {

    private final Logger log = LoggerFactory.getLogger(MonthServiceImpl.class);

    @Inject
    private MonthRepository monthRepository;

    @Inject
    public MonthServiceImpl(MonthRepository repository) {
        super(repository);
    }


}
