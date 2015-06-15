package ro.teamnet.scheduler.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ro.teamnet.bootstrap.service.AbstractServiceImpl;
import ro.teamnet.scheduler.domain.RecurrentTimeUnit;
import ro.teamnet.scheduler.repository.RecurrentTimeUnitRepository;

import javax.inject.Inject;

@Service
public class RecurrentTimeUnitServiceImpl extends AbstractServiceImpl<RecurrentTimeUnit, Long> implements RecurrentTimeUnitService {

    private final Logger log = LoggerFactory.getLogger(RecurrentTimeUnitServiceImpl.class);

    @Inject
    private RecurrentTimeUnitRepository recurrentTimeUnitRepository;

    @Inject
    public RecurrentTimeUnitServiceImpl(RecurrentTimeUnitRepository repository) {
        super(repository);
    }

    @Override
    public Long deleteByScheduleId(Long id) {
        return recurrentTimeUnitRepository.deleteByScheduleId(id);
    }
}
