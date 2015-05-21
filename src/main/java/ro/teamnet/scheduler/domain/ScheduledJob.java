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
 * A ScheduledJob.
 */
@Entity
@Table(name = "T_SCHEDULEDJOB")
public class ScheduledJob implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

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

    @Column(name = "version")
    private Long version;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "created", nullable = false)
    private DateTime created;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "last_updated", nullable = false)
    private DateTime lastUpdated;

    @Column(name = "deleted")
    private Boolean deleted;

    @OneToMany(mappedBy = "scheduledJob", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Schedule> schedules = new HashSet<>();

    @OneToMany(mappedBy = "scheduledJob", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Task> tasks = new HashSet<>();


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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public DateTime getCreated() {
        return created;
    }

    public void setCreated(DateTime created) {
        this.created = created;
    }

    public DateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(DateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Set<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(Set<Schedule> schedules) {
        this.schedules = schedules;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
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

        ScheduledJob scheduledJob = (ScheduledJob) o;

        if (id != null ? !id.equals(scheduledJob.id) : scheduledJob.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "ScheduledJob{" +
                "id=" + id +
                ", name='" + name + "'" +
                ", description='" + description + "'" +
                ", nextScheduledExecution='" + nextScheduledExecution + "'" +
                ", lastExecutionTime='" + lastExecutionTime + "'" +
                ", lastExecutionState='" + lastExecutionState + "'" +
                ", version='" + version + "'" +
                ", created='" + created + "'" +
                ", lastUpdated='" + lastUpdated + "'" +
                ", deleted='" + deleted + "'" +
                '}';
    }
}
