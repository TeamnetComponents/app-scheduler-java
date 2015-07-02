
package ro.teamnet.scheduler.domain;
        

import javax.persistence.*;
import java.io.Serializable;

/**
 * A ExecutionData.
 */
@Entity
@Table(name = "T_EXECUTIONDATA")
public class ExecutionData implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "data_id")
    private Long dataId;

    @ManyToOne
    private Configuration configuration;

    @ManyToOne
    private ScheduledJobExecution scheduledJobExecution;


    //other entity fields relations

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDataId() {
        return dataId;
    }

    public void setDataId(Long dataId) {
        this.dataId = dataId;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public ScheduledJobExecution getScheduledJobExecution() {
        return scheduledJobExecution;
    }

    public void setScheduledJobExecution(ScheduledJobExecution scheduledJobExecution) {
        this.scheduledJobExecution = scheduledJobExecution;
    }


    //other entity methods relations

}
