package ro.teamnet;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ro.teamnet.bootstrap.config.DatabaseConfiguration;
import ro.teamnet.bootstrap.extend.AppRepositoryFactoryBean;

import java.util.List;

@Configuration
@EnableJpaRepositories(basePackages = {"ro.teamnet.scheduler.repository"},
        repositoryFactoryBeanClass = AppRepositoryFactoryBean.class)
public class SchedulerTestDatabaseConfig extends DatabaseConfiguration {

    @Override
    public List<String> getEntityPackagesToScan() {
        List<String> entityPackagesToScan = super.getEntityPackagesToScan();
        entityPackagesToScan.add("ro.teamnet");
        return entityPackagesToScan;
    }
}
