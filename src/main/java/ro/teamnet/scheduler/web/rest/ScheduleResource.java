package ro.teamnet.scheduler.web.rest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ro.teamnet.bootstrap.web.rest.AbstractResource;
import ro.teamnet.scheduler.domain.Schedule;
import ro.teamnet.scheduler.service.CronExpressionService;
import ro.teamnet.scheduler.service.ScheduleService;

import javax.inject.Inject;

/**
 * REST controller for managing Schedule.
 */
@RestController
@RequestMapping("/app/rest/schedule")
public class ScheduleResource extends AbstractResource<Schedule, Long> {

    private final Logger log = LoggerFactory.getLogger(ScheduleResource.class);

    @Inject
    public ScheduleResource(ScheduleService service) {
        super(service);
    }

    @Inject
    private CronExpressionService cronExpressionService;

    @Override
    public void create(@RequestBody Schedule schedule) {

        String cronExpression = "";
        if (!schedule.getRecurrent()) {
            cronExpression = cronExpressionService.buildCronExpressionForRecurrentFalse(schedule);
        } else {
            if (schedule.getTimeInterval() != null) {
                cronExpression = cronExpressionService.buildCronExpressionForRecurrentTrueWithTimeInterval(schedule);
            } else {
                cronExpression = cronExpressionService.buildCronExpressionForRecurrentTrueWithRecurrentTimeUnit(schedule);
            }
        }
        schedule.setCron(cronExpression);

        super.create(schedule);
    }
}
