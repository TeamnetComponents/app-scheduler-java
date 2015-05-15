package ro.teamnet.scheduler.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A TimeInterval.
 */
@Entity
@Table(name = "T_TIMEINTERVAL")
public class TimeInterval implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "custom")
    private Boolean custom;

    @Column(name = "interval_millis")
    private Long intervalMillis;

    @Column(name = "interval")
    private Long interval;

    @ManyToOne
    private TimeUnit timeUnit;

    @OneToMany(mappedBy = "timeInterval", fetch = FetchType.LAZY)
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

    public Boolean getCustom() {
        return custom;
    }

    public void setCustom(Boolean custom) {
        this.custom = custom;
    }

    public Long getIntervalMillis() {
        return intervalMillis;
    }

    public void setIntervalMillis(Long intervalMillis) {
        this.intervalMillis = intervalMillis;
    }

    public Long getInterval() {
        return interval;
    }

    public void setInterval(Long interval) {
        this.interval = interval;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
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

        TimeInterval timeInterval = (TimeInterval) o;

        if (id != null ? !id.equals(timeInterval.id) : timeInterval.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "TimeInterval{" +
                "id=" + id +
                ", name='" + name + "'" +
                ", custom='" + custom + "'" +
                ", intervalMillis='" + intervalMillis + "'" +
                ", interval='" + interval + "'" +
                '}';
    }
}
