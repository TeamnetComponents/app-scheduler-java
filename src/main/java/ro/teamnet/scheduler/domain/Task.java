package ro.teamnet.scheduler.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.persistence.*;
import java.io.Serializable;

/**
 * A Task.
 */
@Entity
@Table(name = "T_TASK")
public class Task implements Serializable, AppJob {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "type")
    private String type;

    @Column(name = "command")
    private String command;

    @Column(name = "options")
    private String options;

    @OneToOne(mappedBy = "task", fetch = FetchType.LAZY)
    @JsonIgnore
    private SchedulableJob schedulableJob;


    //other entity fields relations

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public SchedulableJob getSchedulableJob() {
        return schedulableJob;
    }

    public void setSchedulableJob(SchedulableJob schedulableJob) {
        this.schedulableJob = schedulableJob;
    }


    //other entity methods relations

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Task task = (Task) o;

        if (id != null ? !id.equals(task.id) : task.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", code='" + code + "'" +
                ", type='" + type + "'" +
                ", command='" + command + "'" +
                ", options='" + options + "'" +
                '}';
    }

    @Transient
    private String optionValues;

    @Override
    @Transient
    public void configure(String optionValues) {
        this.optionValues = optionValues;
    }

    @Override
    @Transient
    public void execute(JobExecutionContext context) throws JobExecutionException {
        //TODO do something meaningful
        System.out.println("###############################");
        System.out.println("S-a executat task-ul " + code + " cu optiunile ( " + options + " : " + optionValues + " )");
        System.out.println("###############################");
    }
}
