package ro.teamnet.scheduler.domain;


import javax.persistence.*;
import java.io.Serializable;

/**
 * A Task.
 */
@Entity
@Table(name = "T_TASK")
public class Task implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "type")
    private String type;

    @Column(name = "qrtz_job_class")
    private String quartzJobClassName;

    @Column(name = "options")
    private String options;

    @ManyToOne
    private ScheduledJob scheduledJob;


    //other entity fields relations

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQuartzJobClassName() {
        return quartzJobClassName;
    }

    public void setQuartzJobClassName(String quartzJobClassName) {
        this.quartzJobClassName = quartzJobClassName;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public ScheduledJob getScheduledJob() {
        return scheduledJob;
    }

    public void setScheduledJob(ScheduledJob scheduledJob) {
        this.scheduledJob = scheduledJob;
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
                ", type='" + type + "'" +
                ", quartzJobClassName='" + quartzJobClassName + "'" +
                ", options='" + options + "'" +
                '}';
    }
}
