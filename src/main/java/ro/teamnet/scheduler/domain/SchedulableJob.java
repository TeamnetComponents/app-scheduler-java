package ro.teamnet.scheduler.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import ro.teamnet.bootstrap.domain.util.CustomDateTimeDeserializer;
import ro.teamnet.bootstrap.domain.util.CustomDateTimeSerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A SchedulableJob.
 */
@Entity
@Table(name = "T_SCHEDULABLEJOB")
public class SchedulableJob implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "options")
    private String options;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "next_scheduled_execution", nullable = false)
    private DateTime nextScheduledExecution;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "last_execution_time", nullable = false)
    private DateTime lastExecutionTime;

    @Column(name = "last_execution_state")
    private String lastExecutionState;

    @ManyToOne
    private Task task;

    @OneToMany(mappedBy = "schedulableJob", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Schedule> schedules = new HashSet<>();


    //other entity fields relations

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public DateTime getNextScheduledExecution() {
        return nextScheduledExecution;
    }

    public void setNextScheduledExecution(DateTime nextScheduledExecution) {
        this.nextScheduledExecution = nextScheduledExecution;
    }

    public DateTime getLastExecutionTime() {
        return lastExecutionTime;
    }

    public void setLastExecutionTime(DateTime lastExecutionTime) {
        this.lastExecutionTime = lastExecutionTime;
    }

    public String getLastExecutionState() {
        return lastExecutionState;
    }

    public void setLastExecutionState(String lastExecutionState) {
        this.lastExecutionState = lastExecutionState;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Set<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(Set<Schedule> schedules) {
        this.schedules = schedules;
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

        SchedulableJob schedulableJob = (SchedulableJob) o;

        if (id != null ? !id.equals(schedulableJob.id) : schedulableJob.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "SchedulableJob{" +
                "id=" + id +
                ", name='" + name + "'" +
                ", options='" + options + "'" +
                ", nextScheduledExecution='" + nextScheduledExecution + "'" +
                ", lastExecutionTime='" + lastExecutionTime + "'" +
                ", lastExecutionState='" + lastExecutionState + "'" +
                '}';
    }
}
