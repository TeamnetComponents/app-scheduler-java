package ro.teamnet.scheduler.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import ro.teamnet.bootstrap.config.DatabaseConfiguration;

import javax.inject.Inject;
import javax.sql.DataSource;

/**
 * Created by Oana.Mihai on 5/11/2015.
 */
@Configuration
@AutoConfigureAfter({DatabaseConfiguration.class})
public class QuartzConfiguration {

    private final Logger log = LoggerFactory.getLogger(QuartzConfiguration.class);

    @Inject
    private DataSource dataSource;
    @Inject
    private PlatformTransactionManager transactionManager;

    @Bean
    @ConfigurationProperties(prefix = "scheduler.quartz")
    public SchedulerFactoryBean quartzScheduler() {
        log.debug("Configuring Quartz Scheduler");
        SchedulerFactoryBean quartzScheduler = new SchedulerFactoryBean();
        quartzScheduler.setSchedulerName("app-scheduler");
        quartzScheduler.setDataSource(dataSource);
        quartzScheduler.setTransactionManager(transactionManager);
        quartzScheduler.setOverwriteExistingJobs(true);

        return quartzScheduler;
    }

}
