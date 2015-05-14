
package ro.teamnet.scheduler.service;


import ro.teamnet.bootstrap.service.AbstractServiceImpl;
import org.springframework.stereotype.Service;

import ro.teamnet.scheduler.domain.RecurrentYear;
import ro.teamnet.scheduler.repository.RecurrentYearRepository;
import org.springframework.transaction.annotation.Transactional;
import javax.inject.Inject;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RecurrentYearServiceImpl extends AbstractServiceImpl<RecurrentYear,Long> implements RecurrentYearService {

    private final Logger log = LoggerFactory.getLogger(RecurrentYearServiceImpl.class);

    @Inject
    private RecurrentYearRepository recurrentYearRepository;

    @Inject
    public RecurrentYearServiceImpl(RecurrentYearRepository repository) {
        super(repository);
    }



}
