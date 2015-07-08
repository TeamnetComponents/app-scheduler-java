package ro.teamnet.scheduler.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.plugin.core.config.EnablePluginRegistries;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import ro.teamnet.bootstrap.config.DatabaseConfiguration;
import ro.teamnet.scheduler.service.ExecutionService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by Oana.Mihai on 5/11/2015.
 */
@Configuration
@AutoConfigureAfter({DatabaseConfiguration.class})
@EnablePluginRegistries({ExecutionService.class})
public class QuartzConfiguration {

    private final Logger log = LoggerFactory.getLogger(QuartzConfiguration.class);

    @Inject
    private Environment environment;

    private RelaxedPropertyResolver propertyResolver;

    @Inject
    private DataSource dataSource;
    @Inject
    private PlatformTransactionManager transactionManager;
    @Inject
    private ApplicationContext applicationContext;

    @Bean
    public SchedulerFactoryBean quartzScheduler() {
        log.debug("Configuring Quartz Scheduler");
        SchedulerFactoryBean quartzScheduler = new SchedulerFactoryBean();

        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        quartzScheduler.setJobFactory(jobFactory);

        quartzScheduler.setSchedulerName("app-scheduler");
        quartzScheduler.setOverwriteExistingJobs(false);
        quartzScheduler.setDataSource(dataSource);
        quartzScheduler.setTransactionManager(transactionManager);
        quartzScheduler.setQuartzProperties(getQuartzProperties());
        return quartzScheduler;
    }

    private Properties getQuartzProperties() {
        Properties properties = new Properties();
        String delegateClass = propertyResolver.getProperty("jobStore.driverDelegateClass");
        if (delegateClass != null) {
            properties.put("org.quartz.jobStore.driverDelegateClass", delegateClass);
        }
        return properties;
    }

    @PostConstruct
    public void initialize() {
        this.propertyResolver = new RelaxedPropertyResolver(environment, "org.quartz.");
    }
}
