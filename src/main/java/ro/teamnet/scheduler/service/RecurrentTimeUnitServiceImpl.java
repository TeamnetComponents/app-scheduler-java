
package ro.teamnet.scheduler.service;


import ro.teamnet.bootstrap.service.AbstractServiceImpl;
import org.springframework.stereotype.Service;

import ro.teamnet.scheduler.domain.RecurrentTimeUnit;
import ro.teamnet.scheduler.repository.RecurrentTimeUnitRepository;
import org.springframework.transaction.annotation.Transactional;
import javax.inject.Inject;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RecurrentTimeUnitServiceImpl extends AbstractServiceImpl<RecurrentTimeUnit,Long> implements RecurrentTimeUnitService {

    private final Logger log = LoggerFactory.getLogger(RecurrentTimeUnitServiceImpl.class);

    @Inject
    private RecurrentTimeUnitRepository recurrentTimeUnitRepository;

    @Inject
    public RecurrentTimeUnitServiceImpl(RecurrentTimeUnitRepository repository) {
        super(repository);
    }



}
