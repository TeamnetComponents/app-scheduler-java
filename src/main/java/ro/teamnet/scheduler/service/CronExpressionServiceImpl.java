package ro.teamnet.scheduler.service;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import ro.teamnet.scheduler.domain.RecurrentTimeUnit;
import ro.teamnet.scheduler.domain.Schedule;
import ro.teamnet.scheduler.domain.TimeInterval;
import ro.teamnet.scheduler.domain.TimeUnit;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class CronExpressionServiceImpl implements CronExpressionService {

    @Inject
    private RecurrentTimeUnitService recurrentTimeUnitService;

    @Inject
    private TimeUnitService timeUnitService;

    public String buildCronExpression(Schedule schedule) {
        StringBuilder stringBuilder = new StringBuilder();
        DateTime startDate = schedule.getStartTime();

        if (!schedule.getRecurrent()) {

            stringBuilder = createStringBuilderForRecurrentFalse(startDate);
        } else {
            if (schedule.getTimeInterval() != null) {

                stringBuilder = createStringBuilderForRegularIntervals(startDate, schedule.getTimeInterval(), schedule.getTimeInterval().getTimeUnit());
            } else {
                List<String> codeOfTimeUnits = Arrays.asList("SEC", "MIN", "H", "D", "MON", "W", "Y");
                for (String codeOfTimeUnit : codeOfTimeUnits) {
                    StringBuilder sb = createStringBuilderForEveryTimeUnit(schedule, codeOfTimeUnit);
                    if (codeOfTimeUnit.equals("W")) {
                        if (createStringBuilderForEveryTimeUnit(schedule, "D").toString().equals("*") &&
                                sb.toString().equals("*")) {
                            stringBuilder.append("? ");
                        } else {
                            stringBuilder.append(sb.toString());
                            stringBuilder.append(" ");
                        }
                    } else {
                        stringBuilder.append(sb.toString());
                        stringBuilder.append(" ");
                    }
                }
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
        }
        return stringBuilder.toString();
    }

    public int getCronWeekDayCode(Integer dateWeekDayCode) {

        return ((dateWeekDayCode + 1) % 7) != 0 ? ((dateWeekDayCode + 1) % 7) : 7;
    }

    private StringBuilder createStringBuilderForEveryTimeUnit(Schedule schedule, String codeOfTimeUnit) {
        StringBuilder stringBuilder = new StringBuilder();
        TimeUnit timeUnit = timeUnitService.findByCode(codeOfTimeUnit);
        Set<RecurrentTimeUnit> recurrentTimeUnits = recurrentTimeUnitService.findByScheduleAndTimeUnit(schedule, timeUnit);

        if(recurrentTimeUnits.size() != 1) {
            if (codeOfTimeUnit.equals("W")) {
                for (RecurrentTimeUnit recurrentTimeUnit : recurrentTimeUnits) {
                    buildStringBuilderForDayOfWeek(stringBuilder, recurrentTimeUnit);
                }
            } else {
                for (RecurrentTimeUnit recurrentTimeUnit : recurrentTimeUnits) {
                    buildStringBuilderForTheRestOfTimeUnit(stringBuilder, recurrentTimeUnit);
                }
            }
        } else {
            if (codeOfTimeUnit.equals("W")) {
                for (RecurrentTimeUnit recurrentTimeUnit : recurrentTimeUnits) {
                    if(recurrentTimeUnit.getValue() == -1) {
                        stringBuilder.append("* ");
                    } else {
                        buildStringBuilderForDayOfWeek(stringBuilder, recurrentTimeUnit);
                    }
                }
            } else {
                for (RecurrentTimeUnit recurrentTimeUnit : recurrentTimeUnits) {
                    if(recurrentTimeUnit.getValue() == -1) {
                        stringBuilder.append("* ");
                    } else {
                        buildStringBuilderForTheRestOfTimeUnit(stringBuilder, recurrentTimeUnit);
                    }
                }
            }
        }

        if (stringBuilder.length() != 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        } else {
            if (codeOfTimeUnit.equals("SEC")) {
                stringBuilder.append("0");
            } else {
                stringBuilder.append("*");
            }
        }

        return stringBuilder;
    }

    private void buildStringBuilderForTheRestOfTimeUnit(StringBuilder stringBuilder, RecurrentTimeUnit recurrentTimeUnit) {
        stringBuilder.append(recurrentTimeUnit.getValue());
        stringBuilder.append(",");
    }

    private void buildStringBuilderForDayOfWeek(StringBuilder stringBuilder, RecurrentTimeUnit recurrentTimeUnit) {
        int weekDayCode = getCronWeekDayCode(recurrentTimeUnit.getValue());
        stringBuilder.append(weekDayCode);
        stringBuilder.append(",");
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

    private StringBuilder createStringBuilderForRegularIntervals(DateTime startDate, TimeInterval timeInterval, TimeUnit timeUnit) {
        StringBuilder stringBuilder = new StringBuilder();

        if (timeUnit.getCode().equals("SEC")) {
            if (timeInterval.getInterval() != null) {
                stringBuilder.append(startDate.getSecondOfMinute());
                stringBuilder.append("/");
                stringBuilder.append(timeInterval.getInterval());
                stringBuilder.append(" ");
            } else {
                stringBuilder.append("0 ");
            }
        } else {
            stringBuilder.append("0 ");
        }

        if (timeUnit.getCode().equals("MIN")) {
            if (timeInterval.getInterval() != null) {
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

        if (timeUnit.getCode().equals("H")) {
            if (timeInterval.getInterval() != null) {
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

        if (timeUnit.getCode().equals("D")) {
            if (timeInterval.getInterval() != null) {
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

        if (timeUnit.getCode().equals("MON")) {
            if (timeInterval.getInterval() != null) {
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

        if (timeUnit.getCode().equals("W")) {
            Integer cronWeekDayValue = startDate.getDayOfWeek();
            Integer cronWeekDayCode = getCronWeekDayCode(cronWeekDayValue);
            stringBuilder.append(cronWeekDayCode);
            stringBuilder.append("/7 ");
        } else {
            stringBuilder.append("? ");
        }

        if (timeUnit.getCode().equals("Y")) {
            if (timeInterval.getInterval() != null) {
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
