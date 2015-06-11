package ro.teamnet.scheduler.job;

/**
 * Created by Oana.Mihai on 6/3/2015.
 */
public enum JobExecutionStatus {
    WAITING("Waiting"),
    RUNNING("Running"),
    FINISHED("Finished"),
    FINISHED_WITH_ERRORS("Finished (with errors)"),
    FAILED("Failed"),
    CANCELLED("Cancelled");

    private String statusName;

    JobExecutionStatus(String statusName) {
        this.statusName = statusName;
    }

    public static JobExecutionStatus findByName(String statusName) {
        for (JobExecutionStatus status : values()) {
            if (status.statusName.equals(statusName)){
                return status;
            }
        }
        return null;
    }
}
