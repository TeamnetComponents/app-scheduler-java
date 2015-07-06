package ro.teamnet.scheduler.service;


import org.springframework.stereotype.Service;
import ro.teamnet.bootstrap.service.AbstractServiceImpl;
import ro.teamnet.scheduler.domain.RecurrentTimeUnit;
import ro.teamnet.scheduler.repository.RecurrentTimeUnitRepository;

import javax.inject.Inject;

@Service
public class RecurrentTimeUnitServiceImpl extends AbstractServiceImpl<RecurrentTimeUnit, Long> implements RecurrentTimeUnitService {

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
