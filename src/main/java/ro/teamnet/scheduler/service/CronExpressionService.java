package ro.teamnet.scheduler.service;

import ro.teamnet.bootstrap.service.AbstractService;
import ro.teamnet.scheduler.domain.Schedule;

public interface CronExpressionService {

    String buildCronExpression(Schedule schedule);
    int getCronWeekDayCode(Integer dateWeekDayCode);
}
