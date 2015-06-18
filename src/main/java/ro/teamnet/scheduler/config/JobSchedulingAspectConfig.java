package ro.teamnet.scheduler.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import ro.teamnet.scheduler.aop.JobSchedulingAspect;

@Configuration
@EnableAspectJAutoProxy
public class JobSchedulingAspectConfig {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Bean
    public JobSchedulingAspect jobSchedulingAspect() {
        log.info("Creating job scheduling aspect.");
        return new JobSchedulingAspect();
    }
}
