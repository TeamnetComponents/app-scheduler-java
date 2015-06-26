package ro.teamnet.scheduler.service;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import ro.teamnet.scheduler.enums.TimeUnitCode;
import ro.teamnet.scheduler.domain.RecurrentTimeUnit;
import ro.teamnet.scheduler.domain.Schedule;
import ro.teamnet.scheduler.domain.TimeInterval;
import ro.teamnet.scheduler.domain.TimeUnit;

import java.util.*;

@Service
public class CronExpressionServiceImpl implements CronExpressionService {

    /**
     * Create the cron expression for a given schedule, "not recurrent" case.
     *
     * @param schedule the given schedule
     * @return the cron expression.
     */
    @Override
    public String buildCronExpressionForRecurrentFalse(Schedule schedule) {

        return createStringBuilderForRecurrentFalse(schedule.getStartTime()).toString();
    }

    /**
     * Create the cron expression for the "not recurrent" case.
     *
     * @param startDate to get the values
     * @return the cron expression for the given schedule.
     */
    private StringBuilder createStringBuilderForRecurrentFalse(DateTime startDate) {

        return  (new StringBuilder()).append("0 ").append(startDate.getMinuteOfHour())
                .append(" ").append(startDate.getHourOfDay())
                .append(" ").append(startDate.getDayOfMonth())
                .append(" ").append(startDate.getMonthOfYear())
                .append(" ").append("? ").append(startDate.getYear());
    }

    /**
     * Create the cron expression for the "recurrent with regular intervals" case.
     *
     * @param schedule the given schedule
     * @return the cron expression for the given schedule.
     */
    @Override
    public String buildCronExpressionForRecurrentTrueWithTimeInterval(Schedule schedule) {

        return createStringBuilderForRegularIntervals(schedule.getStartTime(), schedule.getTimeInterval(),
                schedule.getTimeInterval().getTimeUnit()).toString();
    }

    /**
     * Create the cron expression for the "recurrent with regular intervals" case.
     *
     * @param startDate to get the values
     * @param timeInterval to set the period
     * @param timeUnit to know the time unit
     * @return the cron expression for a given schedule.
     */
    private StringBuilder createStringBuilderForRegularIntervals(DateTime startDate, TimeInterval timeInterval, TimeUnit timeUnit) {
        StringBuilder stringBuilder = new StringBuilder();

        if (timeUnit.getCode().equals(TimeUnitCode.SEC)) {
            stringBuilder.append("*").append("/")
                    .append(timeInterval.getInterval()).append(" ")
                    .append("* * * * ? *");
        }

        if (timeUnit.getCode().equals(TimeUnitCode.MIN)) {
            stringBuilder.append("0 ")
                    .append("*").append("/")
                    .append(timeInterval.getInterval()).append(" ")
                    .append("* * * ? *");
        }

        if (timeUnit.getCode().equals(TimeUnitCode.H)) {
            stringBuilder.append("0 ")
                    .append(startDate.getMinuteOfHour()).append(" ")
                    .append("*").append("/")
                    .append(timeInterval.getInterval()).append(" ")
                    .append("* * ? *");
        }

        if (timeUnit.getCode().equals(TimeUnitCode.D)) {
            stringBuilder.append("0 ")
                    .append(startDate.getMinuteOfHour()).append(" ")
                    .append(startDate.getHourOfDay()).append(" ")
                    .append("*").append("/")
                    .append(timeInterval.getInterval()).append(" ")
                    .append("* ? *");
        }

        if (timeUnit.getCode().equals(TimeUnitCode.MON)) {
            stringBuilder.append("0 ")
                    .append(startDate.getMinuteOfHour()).append(" ")
                    .append(startDate.getHourOfDay()).append(" ")
                    .append(startDate.getDayOfMonth()).append(" ")
                    .append("*").append("/")
                    .append(timeInterval.getInterval()).append(" ")
                    .append("? *");
        }

        if(timeUnit.getCode().equals(TimeUnitCode.W)) {
            stringBuilder.append("0 ")
                    .append(startDate.getMinuteOfHour()).append(" ")
                    .append(startDate.getHourOfDay()).append(" ")
                    .append("*").append("/")
                    .append(timeInterval.getInterval() * 7).append(" ")
                    .append(startDate.getMonthOfYear()).append(" ")
                    .append("? *");
        }

        if (timeUnit.getCode().equals(TimeUnitCode.Y)) {
            stringBuilder.append("0 ")
                    .append(startDate.getMinuteOfHour()).append(" ")
                    .append(startDate.getHourOfDay()).append(" ")
                    .append(startDate.getDayOfMonth()).append(" ")
                    .append(startDate.getMonthOfYear()).append(" ")
                    .append("? ")
                    .append("*")
                    .append("/").append(timeInterval.getInterval());
        }

        return stringBuilder;
    }

    /**
     * Create the cron expression for the "recurrent with custom fire time" case.
     *
     * @param schedule
     * @return the cron expression for the given schedule.
     */
    @Override
    public String buildCronExpressionForRecurrentTrueWithRecurrentTimeUnit(Schedule schedule) {

        return createStringBuilderForTimeUnit(schedule);
    }

    /**
     * Build a HashMap with time units as keys and {@link ro.teamnet.scheduler.domain.RecurrentTimeUnit#value} for all time unit as values.
     * Given all the values for one time unit, we build the cron expression.
     *
     * @param schedule the schedule that offers {@link ro.teamnet.scheduler.domain.RecurrentTimeUnit}
     * @return the cron expression for a given schedule.
     */
    private String createStringBuilderForTimeUnit(Schedule schedule) {
        TimeUnitCode[] codeOfTimeUnits = TimeUnitCode.values();
        Map<String, String> timeUnitIntervals = new HashMap<>();
        Set<RecurrentTimeUnit> recurrentTimeUnitSet = schedule.getRecurrentTimeUnits();
        StringBuilder cronExpressionForOneTimeUnit = new StringBuilder();

        for (TimeUnitCode codeOfTimeUnit : codeOfTimeUnits) {
            buildStringBuilderForEveryTimeUnits(recurrentTimeUnitSet, cronExpressionForOneTimeUnit, codeOfTimeUnit);
            if (cronExpressionForOneTimeUnit.length() != 0) {
                cronExpressionForOneTimeUnit.deleteCharAt(cronExpressionForOneTimeUnit.length() - 1);
            }
            timeUnitIntervals.put(String.valueOf(codeOfTimeUnit), cronExpressionForOneTimeUnit.toString());
            cronExpressionForOneTimeUnit.setLength(0);
        }

        return createCronExpressionForAUnitTime(timeUnitIntervals);
    }

    /**
     * Build the cron expression by analyzing the {@link ro.teamnet.scheduler.domain.RecurrentTimeUnit@value} for a given time unit.
     *
     * @param recurrentTimeUnitSet the set with all data
     * @param stringBuilder to build the cron expression for the given time unit
     * @param codeOfTimeUnit the current value of the time unit
     */
    private void buildStringBuilderForEveryTimeUnits(Set<RecurrentTimeUnit> recurrentTimeUnitSet, StringBuilder stringBuilder, TimeUnitCode codeOfTimeUnit) {
        StringBuilder sb = new StringBuilder();

        for (RecurrentTimeUnit recurrentTimeUnit : recurrentTimeUnitSet) {
            if (recurrentTimeUnit.getTimeUnit() == null || recurrentTimeUnit.getTimeUnit().getCode() == null) {
                continue;
            }
            if (recurrentTimeUnit.getTimeUnit().getCode().equals(codeOfTimeUnit)) {
                if (recurrentTimeUnit.getValue() == -1) {
                    sb.append("* ");
                } else {
                    if(codeOfTimeUnit.equals(TimeUnitCode.W)) {
                        int weekDayCode = getCronWeekDayCode(recurrentTimeUnit.getValue());
                        sb.append(weekDayCode).append(",");
                    } else {
                        sb.append(recurrentTimeUnit.getValue()).append(",");
                    }
                }
            }
        }

        stringBuilder.append(sb.toString());
    }

    /**
     * With the HashMap populated, analyze the possible combination between DaysOfWeek and DaysOfMonth, and build the
     * cron expression.
     *
     * @param timeUnitIntervals the map with data
     * @return the final cron expression for the given schedule.
     */
    private String createCronExpressionForAUnitTime(Map<String, String> timeUnitIntervals) {
        StringBuilder stringBuilder = new StringBuilder();

        String dayOfWeek = timeUnitIntervals.get(String.valueOf(TimeUnitCode.W));
        String dayOfMonth = timeUnitIntervals.get(String.valueOf(TimeUnitCode.D));
        if (dayOfMonth.equals("*") || dayOfMonth.length() == 0) {
            if (dayOfWeek.equals("*") || dayOfWeek.length() == 0) {
                timeUnitIntervals.put(String.valueOf(TimeUnitCode.W), "?");
            } else {
                timeUnitIntervals.put(String.valueOf(TimeUnitCode.D), "?");
            }
        } else {
            timeUnitIntervals.put(String.valueOf(TimeUnitCode.W), "?");
        }

        TimeUnitCode[] codeOfTimeUnits = TimeUnitCode.values();
        for (TimeUnitCode codeOfTimeUnit : codeOfTimeUnits) {
            if (timeUnitIntervals.containsKey(String.valueOf(codeOfTimeUnit))) {
                if (timeUnitIntervals.get(String.valueOf(codeOfTimeUnit)).length() != 0) {
                    stringBuilder.append(timeUnitIntervals.get(String.valueOf(codeOfTimeUnit))).append(" ");
                } else {
                    buildFinalCronExpression(stringBuilder, String.valueOf(codeOfTimeUnit));
                }
            } else {
                buildFinalCronExpression(stringBuilder, String.valueOf(codeOfTimeUnit));
            }
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    /**
     * Generate the part of the cron expression that matches the current time unit, and append it to the expression.
     * SEC and MIN time units doesn't have select value from interface, so we need to set them to 0. If you add
     * select values into the interface you must remove its decision branch.
     *
     * @param stringBuilder retain the cron expression
     * @param codeOfTimeUnit the current time unit
     */
    private void buildFinalCronExpression(StringBuilder stringBuilder, String codeOfTimeUnit) {
        if (codeOfTimeUnit.equals(String.valueOf(TimeUnitCode.SEC))) {
            stringBuilder.append("0 ");
        } else if (codeOfTimeUnit.equals(String.valueOf(TimeUnitCode.MIN))) {
            stringBuilder.append("0 ");
        } else {
            stringBuilder.append("* ");
        }
    }

    /**
     * Generate the part of the cron expression that matches the current time unit, and append it to the expression.
     * This is the case when the user doesn't select a value from the interface so we need to settle the value
     * of the current time unit to the value from the start time.
     *
     * @param schedule the schedule that needs the cron expression
     * @param stringBuilder to build the cron expression
     * @param codeOfTimeUnit the current time unit
     */
    private void buildFinalCronExpressionWithoutGettingValueMinusOne(Schedule schedule, StringBuilder stringBuilder, String codeOfTimeUnit) {
        if (codeOfTimeUnit.equals(String.valueOf(TimeUnitCode.SEC))) {
            stringBuilder.append("0 ");
        } else if (codeOfTimeUnit.equals(String.valueOf(TimeUnitCode.MIN))) {
            stringBuilder.append(schedule.getStartTime().getMinuteOfHour()).append(" ");
        } else if(codeOfTimeUnit.equals(String.valueOf(TimeUnitCode.H))) {
            stringBuilder.append(schedule.getStartTime().getHourOfDay()).append(" ");
        } else  if(codeOfTimeUnit.equals(String.valueOf(TimeUnitCode.D))) {
            stringBuilder.append(schedule.getStartTime().getDayOfMonth()).append(" ");
        } else if(codeOfTimeUnit.equals(String.valueOf(TimeUnitCode.MON))) {
            stringBuilder.append(schedule.getStartTime().getMonthOfYear()).append(" ");
        } else if(codeOfTimeUnit.equals(String.valueOf(TimeUnitCode.W))) {
            stringBuilder.append(schedule.getStartTime().getDayOfWeek()).append(" ");
        } else if(codeOfTimeUnit.equals(String.valueOf(TimeUnitCode.Y))) {
            stringBuilder.append(schedule.getStartTime().getYear());
        } else {
            stringBuilder.append("* ");
        }
    }

    @Override
    public int getCronWeekDayCode(Integer dateWeekDayCode) {

        return ((dateWeekDayCode + 1) % 7) != 0 ? ((dateWeekDayCode + 1) % 7) : 7;
    }
}