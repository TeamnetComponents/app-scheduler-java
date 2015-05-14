
package ro.teamnet.scheduler.service;


import ro.teamnet.bootstrap.service.AbstractServiceImpl;
import org.springframework.stereotype.Service;

import ro.teamnet.scheduler.domain.RecurrentHour;
import ro.teamnet.scheduler.repository.RecurrentHourRepository;
import org.springframework.transaction.annotation.Transactional;
import javax.inject.Inject;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RecurrentHourServiceImpl extends AbstractServiceImpl<RecurrentHour,Long> implements RecurrentHourService {

    private final Logger log = LoggerFactory.getLogger(RecurrentHourServiceImpl.class);

    @Inject
    private RecurrentHourRepository recurrentHourRepository;

    @Inject
    public RecurrentHourServiceImpl(RecurrentHourRepository repository) {
        super(repository);
    }



}
