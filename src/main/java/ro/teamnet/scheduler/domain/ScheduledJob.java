
package ro.teamnet.scheduler.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.SortNatural;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import ro.teamnet.bootstrap.domain.util.CustomDateTimeDeserializer;
import ro.teamnet.bootstrap.domain.util.CustomDateTimeSerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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

    @OneToMany(mappedBy = "scheduledJob", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Schedule> schedules = new HashSet<>();

    @OneToMany(mappedBy = "scheduledJob", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonManagedReference
    @SortNatural
    private SortedSet<ScheduledJobExecution> scheduledJobExecutions = new TreeSet<>();

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

    public Set<ScheduledJobExecution> getScheduledJobExecutions() {
        return scheduledJobExecutions;
    }

    public void setScheduledJobExecutions(SortedSet<ScheduledJobExecution> scheduledJobExecutions) {
        this.scheduledJobExecutions = scheduledJobExecutions;
    }

    @JsonProperty
    public ScheduledJobExecution getScheduledJobExecution() {
        if (scheduledJobExecutions.iterator().hasNext()) {
            return scheduledJobExecutions.iterator().next();
        } else {
            return null;
        }
    }

    //other entity methods relations

    @PrePersist
    private void prePersist() {
        DateTime currentTime = new DateTime();
        setCreated(currentTime);
        setLastUpdated(currentTime);
        setDeleted(false);
    }

    @PreUpdate
    private void preUpdate() {
        setLastUpdated(new DateTime());
    }
}