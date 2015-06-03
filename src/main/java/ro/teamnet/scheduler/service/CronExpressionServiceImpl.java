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

    public String buildCronExpressionForRecurrentFalse(Schedule schedule) {

        return createStringBuilderForRecurrentFalse(schedule.getStartTime()).toString();
    }

    private StringBuilder createStringBuilderForRecurrentFalse(DateTime startDate) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("0 ");
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


    public String buildCronExpressionForRecurrentTrueWithTimeInterval(Schedule schedule) {

        return createStringBuilderForRegularIntervals(schedule.getStartTime(), schedule.getTimeInterval(),
                schedule.getTimeInterval().getTimeUnit()).toString();
    }

    private StringBuilder createStringBuilderForRegularIntervals(DateTime startDate, TimeInterval timeInterval, TimeUnit timeUnit) {
        StringBuilder stringBuilder = new StringBuilder();

        if (timeUnit.getCode().equals("SEC")) {
            stringBuilder.append(startDate.getSecondOfMinute());
            stringBuilder.append("/");
            stringBuilder.append(timeInterval.getInterval());
            stringBuilder.append(" ");
        } else {
            stringBuilder.append("0 ");
        }

        if (timeUnit.getCode().equals("MIN")) {
            stringBuilder.append(startDate.getMinuteOfHour());
            stringBuilder.append("/");
            stringBuilder.append(timeInterval.getInterval());
            stringBuilder.append(" ");
        } else {
            stringBuilder.append(startDate.getMinuteOfHour());
            stringBuilder.append(" ");
        }

        if (timeUnit.getCode().equals("H")) {
            stringBuilder.append(startDate.getHourOfDay());
            stringBuilder.append("/");
            stringBuilder.append(timeInterval.getInterval());
            stringBuilder.append(" ");
        } else {
            stringBuilder.append(startDate.getHourOfDay());
            stringBuilder.append(" ");
        }

        if (timeUnit.getCode().equals("D")) {
            stringBuilder.append(startDate.getDayOfMonth());
            stringBuilder.append("/");
            stringBuilder.append(timeInterval.getInterval());
            stringBuilder.append(" ");
        } else {
            stringBuilder.append(startDate.getDayOfMonth());
            stringBuilder.append(" ");
        }

        if (timeUnit.getCode().equals("MON")) {
            stringBuilder.append(startDate.getMonthOfYear());
            stringBuilder.append("/");
            stringBuilder.append(timeInterval.getInterval());
            stringBuilder.append(" ");
        } else {
            stringBuilder.append(startDate.getMonthOfYear());
            stringBuilder.append(" ");
        }

        if (timeUnit.getCode().equals("W")) {
            Integer cronWeekDayValue = startDate.getDayOfWeek();
            Integer cronWeekDayCode = getCronWeekDayCode(cronWeekDayValue);
            stringBuilder.append(cronWeekDayCode);
            stringBuilder.append("/7 ");
        } else {
            stringBuilder.append("? ");
        }

        if (timeUnit.getCode().equals("Y")) {
            stringBuilder.append(startDate.getYear());
            stringBuilder.append("/");
            stringBuilder.append(timeInterval.getInterval());
        } else {
            stringBuilder.append(startDate.getYear());
        }

        return stringBuilder;
    }


    public String buildCronExpressionForRecurrentTrueWithRecurrentTimeUnit(Schedule schedule) {

        return createStringBuilderForTimeUnit(schedule);
    }

    private String createStringBuilderForTimeUnit(Schedule schedule) {
        List<String> codeOfTimeUnits = Arrays.asList("SEC", "MIN", "H", "D", "MON", "W", "Y");
        Map<String, String> timeUnitIntervals = new HashMap<>();
        Set<RecurrentTimeUnit> recurrentTimeUnitSet = schedule.getRecurrentTimeUnits();
        StringBuilder cronExpressionForOneTimeUnit = new StringBuilder();

        for (String codeOfTimeUnit : codeOfTimeUnits) {
            if (codeOfTimeUnit.equals("W")) {
                buildStringBuilderForDayOfWeek(recurrentTimeUnitSet, cronExpressionForOneTimeUnit, codeOfTimeUnit);
            } else {
                buildStringBuilderForTheRestOfTimeUnits(recurrentTimeUnitSet, cronExpressionForOneTimeUnit, codeOfTimeUnit);
            }

            if (cronExpressionForOneTimeUnit.length() != 0) {
                cronExpressionForOneTimeUnit.deleteCharAt(cronExpressionForOneTimeUnit.length() - 1);
            }
            timeUnitIntervals.put(codeOfTimeUnit, cronExpressionForOneTimeUnit.toString());
            cronExpressionForOneTimeUnit.setLength(0);
        }

        return createCronExpressionForAUnitTime(schedule, timeUnitIntervals);
    }

    private void buildStringBuilderForDayOfWeek(Set<RecurrentTimeUnit> recurrentTimeUnitSet, StringBuilder stringBuilder, String codeOfTimeUnit) {
        StringBuilder sb = new StringBuilder();

        for (RecurrentTimeUnit recurrentTimeUnit : recurrentTimeUnitSet) {
            if (recurrentTimeUnit.getTimeUnit() == null || recurrentTimeUnit.getTimeUnit().getCode() == null) {
                continue;
            }
            if (recurrentTimeUnit.getTimeUnit().getCode().equals(codeOfTimeUnit)) {
                if (recurrentTimeUnit.getValue() == -1) {
                    sb.append("? ");
                } else {
                    int weekDayCode = getCronWeekDayCode(recurrentTimeUnit.getValue());
                    sb.append(weekDayCode);
                    sb.append(",");
                }
            }
        }

        stringBuilder.append(sb.toString());
    }

    private void buildStringBuilderForTheRestOfTimeUnits(Set<RecurrentTimeUnit> recurrentTimeUnitSet, StringBuilder cronExpressionForOneTimeUnit, String codeOfTimeUnit) {
        StringBuilder sb = new StringBuilder();

        for (RecurrentTimeUnit recurrentTimeUnit : recurrentTimeUnitSet) {
            if (recurrentTimeUnit.getTimeUnit() == null || recurrentTimeUnit.getTimeUnit().getCode() == null) {
                continue;
            }
            if (recurrentTimeUnit.getTimeUnit().getCode().equals(codeOfTimeUnit)) {
                if (recurrentTimeUnit.getValue() == -1) {
                    sb.append("* ");
                } else {
                    cronExpressionForOneTimeUnit.append(recurrentTimeUnit.getValue());
                    cronExpressionForOneTimeUnit.append(",");
                }
            }
        }

        cronExpressionForOneTimeUnit.append(sb.toString());
    }

    private String createCronExpressionForAUnitTime(Schedule schedule, Map<String, String> timeUnitIntervals) {
        StringBuilder stringBuilder = new StringBuilder();

        String dayOfWeek = timeUnitIntervals.get("W");
        String dayOfMonth = timeUnitIntervals.get("D");
        if (dayOfMonth.equals("*") || dayOfMonth.length() == 0) {
            if (dayOfWeek.equals("*") || dayOfWeek.length() == 0) {
                timeUnitIntervals.put("W", "?");
            } else {
                timeUnitIntervals.put("D", "?");
            }
        } else {
            timeUnitIntervals.put("W", "?");
        }

        List<String> codeOfTimeUnits = Arrays.asList("SEC", "MIN", "H", "D", "MON", "W", "Y");
        for (String codeOfTimeUnit : codeOfTimeUnits) {
            if (timeUnitIntervals.containsKey(codeOfTimeUnit)) {
                if (timeUnitIntervals.get(codeOfTimeUnit).length() != 0) {
                    stringBuilder.append(timeUnitIntervals.get(codeOfTimeUnit));
                    stringBuilder.append(" ");
                } else {
                    buildFinalCronExpression(schedule, stringBuilder, codeOfTimeUnit);
                }
            } else {
                buildFinalCronExpression(schedule, stringBuilder, codeOfTimeUnit);
            }
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    private void buildFinalCronExpression(Schedule schedule, StringBuilder stringBuilder, String codeOfTimeUnit) {
        if (codeOfTimeUnit.equals("SEC")) {
            stringBuilder.append("0 ");
        } else if (codeOfTimeUnit.equals("MIN")) {
            stringBuilder.append(schedule.getStartTime().getMinuteOfHour());
            stringBuilder.append(" ");
        } else {
            stringBuilder.append("* ");
        }
    }


    public int getCronWeekDayCode(Integer dateWeekDayCode) {

        return ((dateWeekDayCode + 1) % 7) != 0 ? ((dateWeekDayCode + 1) % 7) : 7;
    }
}