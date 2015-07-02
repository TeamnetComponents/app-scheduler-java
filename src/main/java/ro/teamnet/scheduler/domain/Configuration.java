
package ro.teamnet.scheduler.domain;
        

import ro.teamnet.scheduler.dto.ConfigurationDTO;

import javax.persistence.*;
import java.io.Serializable;

/**
 * A Configuration.
 */
@Entity
@Table(name = "T_CONFIGURATION")
public class Configuration implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "configuration_id")
    private Long configurationId;

    @Column(name = "type")
    private String type;

    @ManyToOne
    private ScheduledJob scheduledJob;

    public Configuration() {

    }

    public Configuration(ConfigurationDTO dto) {
        configurationId = dto.getConfigurationId();
        type = dto.getType();
    }

    //other entity fields relations

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConfigurationId() {
        return configurationId;
    }

    public void setConfigurationId(Long configurationId) {
        this.configurationId = configurationId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ScheduledJob getScheduledJob() {
        return scheduledJob;
    }

    public void setScheduledJob(ScheduledJob scheduledJob) {
        this.scheduledJob = scheduledJob;
    }

    //other entity methods relations

}
