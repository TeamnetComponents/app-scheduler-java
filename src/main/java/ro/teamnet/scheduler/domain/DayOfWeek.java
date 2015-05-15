package ro.teamnet.scheduler.domain;


import javax.persistence.*;
import java.io.Serializable;

/**
 * A DayOfWeek.
 */
@Entity
@Table(name = "T_DAYOFWEEK")
public class DayOfWeek implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "value")
    private Integer value;

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

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
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

        DayOfWeek dayOfWeek = (DayOfWeek) o;

        if (id != null ? !id.equals(dayOfWeek.id) : dayOfWeek.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "DayOfWeek{" +
                "id=" + id +
                ", code='" + code + "'" +
                ", name='" + name + "'" +
                ", value='" + value + "'" +
                '}';
    }
}
