package ro.teamnet.scheduler.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
 * A Schedule.
 */
@Entity
@Table(name = "T_SCHEDULE")
public class Schedule implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "recurrent")
    private Boolean recurrent;

    @Column(name = "cron")
    private String cron;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "start_time", nullable = false)
    private DateTime startTime;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "end_time", nullable = true)
    private DateTime endTime;

    @Column(name = "repetitions")
    private Long repetitions;

    @Version
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

    @ManyToOne
    private TimeInterval timeInterval;

    @OneToMany(mappedBy = "schedule", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<RecurrentTimeUnit> recurrentTimeUnits = new HashSet<>();

    @ManyToOne
    private ScheduledJob scheduledJob;


    //other entity fields relations

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getRecurrent() {
        return recurrent;
    }

    public void setRecurrent(Boolean recurrent) {
        this.recurrent = recurrent;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    public Long getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(Long repetitions) {
        this.repetitions = repetitions;
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

    public TimeInterval getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(TimeInterval timeInterval) {
        this.timeInterval = timeInterval;
    }

    public Set<RecurrentTimeUnit> getRecurrentTimeUnits() {
        return recurrentTimeUnits;
    }

    public void setRecurrentTimeUnits(Set<RecurrentTimeUnit> recurrentTimeUnits) {
        this.recurrentTimeUnits = recurrentTimeUnits;
    }

    public ScheduledJob getScheduledJob() {
        return scheduledJob;
    }

    public void setScheduledJob(ScheduledJob scheduledJob) {
        this.scheduledJob = scheduledJob;
    }


    //other entity methods relations


    @PrePersist
    private void prePersist(){
        DateTime currentTime = new DateTime();
        setCreated(currentTime);
        setLastUpdated(currentTime);
        setDeleted(false);
    }

    @PreUpdate
    private void preUpdate() {
        setLastUpdated(new DateTime());
    }

    @Transient
    @JsonIgnore
    public String getTriggerName(){
        return "Schedule_" + id;
    }

    @Transient
    @JsonIgnore
    public String getTriggerGroup(){
        return scheduledJob.getTriggerGroup();
    }

    @Transient
    @JsonIgnore
    public boolean isValid() {
        return !deleted && active && (cron != null);
    }
}
