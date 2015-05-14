
package ro.teamnet.scheduler.service;


import ro.teamnet.bootstrap.service.AbstractServiceImpl;
import org.springframework.stereotype.Service;

import ro.teamnet.scheduler.domain.RecurrentMinute;
import ro.teamnet.scheduler.repository.RecurrentMinuteRepository;
import org.springframework.transaction.annotation.Transactional;
import javax.inject.Inject;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RecurrentMinuteServiceImpl extends AbstractServiceImpl<RecurrentMinute,Long> implements RecurrentMinuteService {

    private final Logger log = LoggerFactory.getLogger(RecurrentMinuteServiceImpl.class);

    @Inject
    private RecurrentMinuteRepository recurrentMinuteRepository;

    @Inject
    public RecurrentMinuteServiceImpl(RecurrentMinuteRepository repository) {
        super(repository);
    }



}
