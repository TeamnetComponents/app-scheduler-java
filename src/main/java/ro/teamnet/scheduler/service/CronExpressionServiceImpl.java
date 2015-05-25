package ro.teamnet.scheduler.service;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import ro.teamnet.scheduler.domain.Schedule;
import ro.teamnet.scheduler.domain.TimeInterval;
import ro.teamnet.scheduler.domain.TimeUnit;
import ro.teamnet.scheduler.repository.ScheduleRepository;

import javax.inject.Inject;

@Service
public class CronExpressionServiceImpl implements CronExpressionService {

    @Inject
    private ScheduleRepository scheduleRepository;

    @Inject
    private TimeIntervalService timeIntervalService;

    @Inject
    private TimeUnitService timeUnitService;

    public String buildCronExpression(Schedule schedule) {
        StringBuilder stringBuilder = new StringBuilder();
        if(schedule.getRecurrent() == false) {

            DateTime startdate = schedule.getStartTime();
            stringBuilder.append("0 ");
            stringBuilder.append(startdate.getMinuteOfHour() + " ");
            stringBuilder.append(startdate.getHourOfDay() + " ");
            stringBuilder.append(startdate.getDayOfMonth() + " ");
            stringBuilder.append(startdate.getMonthOfYear() + " ");
            //stringBuilder.append(startdate.getDayOfWeek() + " ");
            stringBuilder.append("? ");
            stringBuilder.append(startdate.getYear() + " ");
        } else {
            if(schedule.getTimeInterval() != null) {
                //intervale
                //TimeInterval timeInterval = timeIntervalService.findById(schedule.getTimeInterval().getId());
                //TimeUnit timeUnit = timeUnitService.findById(schedule.getTimeInterval().getTimeUnit().getId());

                stringBuilder = createStringBuilder(schedule, schedule.getTimeInterval());
            } else {
                //fara intervale

            }
        }

        return stringBuilder.toString();
    }

    private StringBuilder createStringBuilder(Schedule schedule, TimeInterval timeInterval) {
        StringBuilder stringBuilder = new StringBuilder();
        DateTime startdate = schedule.getStartTime();

        stringBuilder.append("0 ");

        if(timeInterval.getName().equals("In fiecare minut")) {
            stringBuilder.append("* ");
        } else {
            stringBuilder.append(startdate.getMinuteOfHour() + " ");
        }

        if(timeInterval.getName().equals("In fiecare ora")) {
            stringBuilder.append(startdate.getHourOfDay() + " ");
        } else {
            stringBuilder.append("* ");
        }

        if(timeInterval.getName().equals("Zilnic")) {
            stringBuilder.append(startdate.getDayOfMonth() + " ");
        } else {
            stringBuilder.append("* ");
        }

        if(timeInterval.getName().equals("Lunar")) {
            stringBuilder.append(startdate.getMonthOfYear() + " ");
        } else {
            stringBuilder.append("* ");
        }

        if(timeInterval.getName().equals("Saptamanal")) {
            stringBuilder.append(startdate.getDayOfWeek() + " ");
        } else {
            stringBuilder.append(startdate.getDayOfWeek() + "/7 ");
        }

        if(timeInterval.getName().equals("Anual")) {
            stringBuilder.append(startdate.getYear() + " ");
        } else {
            stringBuilder.append("*");
        }

        return stringBuilder;
    }
}
