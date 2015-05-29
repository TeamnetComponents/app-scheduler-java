package ro.teamnet.scheduler.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A TimeUnit.
 */
@Entity
@Table(name = "T_TIMEUNIT")
public class TimeUnit implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "millis")
    private Long millis;

    @OneToMany(mappedBy = "timeUnit", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<TimeInterval> timeIntervals = new HashSet<>();

    @OneToMany(mappedBy = "timeUnit", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<RecurrentTimeUnit> recurrentTimeUnits = new HashSet<>();


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

    public Long getMillis() {
        return millis;
    }

    public void setMillis(Long millis) {
        this.millis = millis;
    }

    public Set<TimeInterval> getTimeIntervals() {
        return timeIntervals;
    }

    public void setTimeIntervals(Set<TimeInterval> timeIntervals) {
        this.timeIntervals = timeIntervals;
    }

    public Set<RecurrentTimeUnit> getRecurrentTimeUnits() {
        return recurrentTimeUnits;
    }

    public void setRecurrentTimeUnits(Set<RecurrentTimeUnit> recurrentTimeUnits) {
        this.recurrentTimeUnits = recurrentTimeUnits;
    }


    //other entity methods relations

}
