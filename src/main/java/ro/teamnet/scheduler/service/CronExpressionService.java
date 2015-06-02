package ro.teamnet.scheduler.service;

import ro.teamnet.bootstrap.service.AbstractService;
import ro.teamnet.scheduler.domain.Schedule;

public interface CronExpressionService {

    int getCronWeekDayCode(Integer dateWeekDayCode);
    String buildCronExpressionForRecurrentFalse(Schedule schedule);
    String buildCronExpressionForRecurrentTrueWithTimeInterval(Schedule schedule);
    String buildCronExpressionForRecurrentTrueWithRecurrentTimeUnit(Schedule schedule);
}
