
package ro.teamnet.scheduler.service;


import ro.teamnet.bootstrap.service.AbstractServiceImpl;
import org.springframework.stereotype.Service;

import ro.teamnet.scheduler.domain.RecurrentDay;
import ro.teamnet.scheduler.repository.RecurrentDayRepository;
import org.springframework.transaction.annotation.Transactional;
import javax.inject.Inject;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RecurrentDayServiceImpl extends AbstractServiceImpl<RecurrentDay,Long> implements RecurrentDayService {

    private final Logger log = LoggerFactory.getLogger(RecurrentDayServiceImpl.class);

    @Inject
    private RecurrentDayRepository recurrentDayRepository;

    @Inject
    public RecurrentDayServiceImpl(RecurrentDayRepository repository) {
        super(repository);
    }



}
