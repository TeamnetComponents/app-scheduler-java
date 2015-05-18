//package ro.teamnet.scheduler.service;
//
//import org.quartz.*;
//import org.springframework.stereotype.Service;
//import ro.teamnet.scheduler.domain.SchedulableJob;
//import ro.teamnet.scheduler.domain.Schedule;
//
//import javax.annotation.PostConstruct;
//import javax.inject.Inject;
//import java.util.List;
//
///**
// * Scheduling service. Reads stored jobs from the database and passes them to the Quartz scheduler.
// */
//@Service
//public class QuartzService {
//
//    @Inject
//    private Scheduler scheduler;
//
//    @Inject
//    ScheduleService scheduleService;
//
//    @PostConstruct
//    private void setupJobs() {
//        List<Schedule> schedules = scheduleService.findAll();
//
//        for (Schedule schedule : schedules) {
//            SchedulableJob schedulableJob = schedule.getSchedulableJob();
//            if (!schedule.getActive() || schedulableJob == null) {
//                continue;
//            }
//            JobDetail jobDetail = JobBuilder.newJob(schedulableJob.getTask().getClass()).build();
//            ScheduleBuilder schedBuilder = schedule.getCron() == null ?
//                    SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(10).withRepeatCount(10) :
//                    CronScheduleBuilder.cronSchedule(schedule.getCron());
//            Trigger trigger = TriggerBuilder.newTrigger().startNow().withSchedule(schedBuilder).build();
//            try {
//                scheduler.scheduleJob(jobDetail, trigger);
//            } catch (SchedulerException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}