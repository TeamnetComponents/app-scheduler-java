package ro.teamnet.scheduler.domain;


import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.io.Serializable;

/**
 * A RecurrentTimeUnit.
 */
@Entity
@Table(name = "T_RECURRENTTIMEUNIT")
public class RecurrentTimeUnit implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "value")
    private Integer value;

    @ManyToOne
    private TimeUnit timeUnit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonBackReference
    private Schedule schedule;


    //other entity fields relations

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }


    //other entity methods relations


}
