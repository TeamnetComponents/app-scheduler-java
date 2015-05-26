package ro.teamnet.scheduler.service;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import ro.teamnet.scheduler.domain.RecurrentTimeUnit;
import ro.teamnet.scheduler.domain.Schedule;
import ro.teamnet.scheduler.domain.TimeInterval;
import ro.teamnet.scheduler.domain.TimeUnit;

import java.util.*;

@Service
public class CronExpressionServiceImpl implements CronExpressionService {

    public String buildCronExpression(Schedule schedule) {
        StringBuilder stringBuilder = new StringBuilder();
        DateTime startDate = schedule.getStartTime();

        if(!schedule.getRecurrent()) {

            stringBuilder = createStringBuilderForRecurrentFalse(startDate);
        } else {
            if(schedule.getTimeInterval() != null) {

                stringBuilder = createStringBuilder(startDate, schedule.getTimeInterval(), schedule.getTimeInterval().getTimeUnit());
            } else {

                Set<RecurrentTimeUnit> recurrentTimeUnits = schedule.getRecurrentTimeUnits();
                stringBuilder.append(createListForEveryTimeUnit(recurrentTimeUnits, "SEC"));
                stringBuilder.append(" ");
                stringBuilder.append(createListForEveryTimeUnit(recurrentTimeUnits, "MIN"));
                stringBuilder.append(" ");
                stringBuilder.append(createListForEveryTimeUnit(recurrentTimeUnits, "H"));
                stringBuilder.append(" ");
                stringBuilder.append(createListForEveryTimeUnit(recurrentTimeUnits, "D"));
                stringBuilder.append(" ");
                stringBuilder.append(createListForEveryTimeUnit(recurrentTimeUnits, "M"));
                stringBuilder.append(" ");
                stringBuilder.append(createListForEveryTimeUnit(recurrentTimeUnits, "W"));
                stringBuilder.append(" ");
                stringBuilder.append(createListForEveryTimeUnit(recurrentTimeUnits, "Y"));
                stringBuilder.append(" ");
            }
        }

        return stringBuilder.toString();
    }

    private String createListForEveryTimeUnit(Set<RecurrentTimeUnit> recurrentTimeUnits, String typeOfTimeUnit) {
        StringBuilder stringBuilder = new StringBuilder();

        for(RecurrentTimeUnit recurrentTimeUnit : recurrentTimeUnits) {
            if(recurrentTimeUnit.getTimeUnit().getCode().equals(typeOfTimeUnit)) {
                stringBuilder.append(recurrentTimeUnit.getValue());
                stringBuilder.append(",");
            }
        }
        if(stringBuilder.length() != 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        } else {
            stringBuilder.append("*");
        }

        return stringBuilder.toString();
    }

    private StringBuilder createStringBuilderForRecurrentFalse(DateTime startDate) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(startDate.getSecondOfMinute());
        stringBuilder.append(" ");
        stringBuilder.append(startDate.getMinuteOfHour());
        stringBuilder.append(" ");
        stringBuilder.append(startDate.getHourOfDay());
        stringBuilder.append(" ");
        stringBuilder.append(startDate.getDayOfMonth());
        stringBuilder.append(" ");
        stringBuilder.append(startDate.getMonthOfYear());
        stringBuilder.append(" ");

        stringBuilder.append("? ");
        stringBuilder.append(startDate.getYear());

        return stringBuilder;
    }

    private StringBuilder createStringBuilder(DateTime startDate, TimeInterval timeInterval, TimeUnit timeUnit) {
        StringBuilder stringBuilder = new StringBuilder();

        if(timeUnit.getCode().equals("SEC")) {
            if(timeInterval.getInterval() != null) {
                stringBuilder.append(startDate.getSecondOfMinute());
                stringBuilder.append("/");
                stringBuilder.append(timeInterval.getInterval());
                stringBuilder.append(" ");
            } else {
                stringBuilder.append("0 ");
            }
        } else {
            stringBuilder.append(startDate.getSecondOfMinute());
            stringBuilder.append(" ");
        }

        if(timeUnit.getCode().equals("MIN")) {
            if(timeInterval.getInterval() != null) {
                stringBuilder.append(startDate.getMinuteOfHour());
                stringBuilder.append("/");
                stringBuilder.append(timeInterval.getInterval());
                stringBuilder.append(" ");
            } else {
                stringBuilder.append("* ");
            }
        } else {
            stringBuilder.append(startDate.getMinuteOfHour());
            stringBuilder.append(" ");
        }

        if(timeUnit.getCode().equals("H")) {
            if(timeInterval.getInterval() != null) {
                stringBuilder.append(startDate.getHourOfDay());
                stringBuilder.append("/");
                stringBuilder.append(timeInterval.getInterval());
                stringBuilder.append(" ");
            } else {
                stringBuilder.append("* ");
            }
        } else {
            stringBuilder.append(startDate.getHourOfDay());
            stringBuilder.append(" ");
        }

        if(timeUnit.getCode().equals("D")) {
            if(timeInterval.getInterval() != null) {
                stringBuilder.append(startDate.getDayOfMonth());
                stringBuilder.append("/");
                stringBuilder.append(timeInterval.getInterval());
                stringBuilder.append(" ");
            } else {
                stringBuilder.append("* ");
            }
        } else {
            stringBuilder.append(startDate.getDayOfMonth());
            stringBuilder.append(" ");
        }

        if(timeUnit.getCode().equals("M")) {
            if(timeInterval.getInterval() != null) {
                stringBuilder.append(startDate.getMonthOfYear());
                stringBuilder.append("/");
                stringBuilder.append(timeInterval.getInterval());
                stringBuilder.append(" ");
            } else {
                stringBuilder.append("* ");
            }
        } else {
            stringBuilder.append(startDate.getMonthOfYear());
            stringBuilder.append(" ");
        }

        if(timeUnit.getCode().equals("W")) {
            if(timeInterval.getInterval() != null) {
                stringBuilder.append(startDate.getMinuteOfHour());
                stringBuilder.append("/");
                stringBuilder.append(timeInterval.getInterval());
                stringBuilder.append(" ");
            } else {
                stringBuilder.append(startDate.getDayOfWeek());
                stringBuilder.append("/7 ");
            }
        } else {
            stringBuilder.append(startDate.getDayOfWeek());
            stringBuilder.append(" ");
        }

        if(timeUnit.getCode().equals("Y")) {
            if(timeInterval.getInterval() != null) {
                stringBuilder.append(startDate.getYear());
                stringBuilder.append("/");
                stringBuilder.append(timeInterval.getInterval());
            } else {
                stringBuilder.append("*");
            }
        } else {
            stringBuilder.append(startDate.getYear());
        }

        return stringBuilder;
    }
}
