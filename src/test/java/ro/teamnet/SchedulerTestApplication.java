package ro.teamnet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import ro.teamnet.bootstrap.async.FileDeleteTask;
import ro.teamnet.bootstrap.config.*;
import ro.teamnet.bootstrap.config.apidoc.SwaggerConfiguration;
import ro.teamnet.bootstrap.config.metrics.JHipsterHealthIndicatorConfiguration;
import ro.teamnet.bootstrap.config.metrics.JavaMailHealthIndicator;
import ro.teamnet.bootstrap.plugin.upload.FileServicePlugin;
import ro.teamnet.bootstrap.service.MailService;
import ro.teamnet.bootstrap.service.SavedFileService;
import ro.teamnet.bootstrap.service.UploadFileLogService;
import ro.teamnet.bootstrap.web.rest.FileDownloadResource;
import ro.teamnet.bootstrap.web.rest.FileUploadResource;
import ro.teamnet.scheduler.config.QuartzConfiguration;
import ro.teamnet.scheduler.service.JobSchedulingServiceImpl;

/**
 * Configuration class for testing Scheduler  module.
 */
@Configuration
@ComponentScan(
        basePackages = {"ro.teamnet.bootstrap", "ro.teamnet"},
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                value = {QuartzConfiguration.class, JobSchedulingServiceImpl.class,
                        FileDownloadResource.class, FileUploadResource.class, FileUploadTokenConfiguration.class,
                        FileDeleteTask.class, FileServicePlugin.class, SavedFileService.class, UploadFileLogService.class,
                        MetricsConfiguration.class, SwaggerConfiguration.class, WebConfigurer.class,
                        ThymeleafConfiguration.class, MailConfiguration.class, MailService.class, JavaMailHealthIndicator.class,
                        CacheConfiguration.class, AsyncConfiguration.class, LoggingAspectConfiguration.class,
                        JHipsterHealthIndicatorConfiguration.class, CloudDatabaseConfiguration.class, LocaleConfiguration.class
                }
        )
)
@EnableAutoConfiguration(exclude = DatabaseConfiguration.class)
public class SchedulerTestApplication {


    /**
     * Main method, used to run the application.
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SchedulerTestApplication.class);
        app.setShowBanner(false);

        SimpleCommandLinePropertySource source = new SimpleCommandLinePropertySource(args);

        // Check if the selected profile has been set as argument.
        // if not the development profile will be added
        addDefaultProfile(app, source);
        addLiquibaseScanPackages();
        app.run(args);
    }

    /**
     * Set a default profile if it has not been set
     */
    protected static void addDefaultProfile(SpringApplication app, SimpleCommandLinePropertySource source) {
        if (!source.containsProperty("spring.profiles.active")) {
            app.setAdditionalProfiles("test-scheduler");
        }
    }

    /**
     * Set the liquibases.scan.packages to avoid an exception from ServiceLocator.
     */
    protected static void addLiquibaseScanPackages() {
        System.setProperty("liquibase.scan.packages", "liquibase.change" + "," + "liquibase.database" + "," +
                "liquibase.parser" + "," + "liquibase.precondition" + "," + "liquibase.datatype" + "," +
                "liquibase.serializer" + "," + "liquibase.sqlgenerator" + "," + "liquibase.executor" + "," +
                "liquibase.snapshot" + "," + "liquibase.logging" + "," + "liquibase.diff" + "," +
                "liquibase.structure" + "," + "liquibase.structurecompare" + "," + "liquibase.lockservice" + "," +
                "liquibase.ext" + "," + "liquibase.changelog");
    }
}