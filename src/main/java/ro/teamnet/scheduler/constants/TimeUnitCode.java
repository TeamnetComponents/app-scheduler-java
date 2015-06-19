package ro.teamnet.scheduler.constants;

public enum TimeUnitCode {

    SEC("SEC"),
    MIN("MIN"),
    H("H"),
    D("D"),
    MON("MON"),
    W("W"),
    Y("y");

    private String timeUnit;

    TimeUnitCode(String timeUnit) {
        this.timeUnit = timeUnit;
    }
}
