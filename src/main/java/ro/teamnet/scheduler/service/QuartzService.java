//package ro.teamnet.scheduler.service;
//
//import org.quartz.*;
//import org.springframework.stereotype.Service;
//import ro.teamnet.scheduler.domain.Schedule;
//import ro.teamnet.scheduler.domain.ScheduledJob;
//import ro.teamnet.scheduler.domain.Task;
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
//    @Inject
//    TaskService taskService;
//
//    @PostConstruct
//    private void setupJobs() {
//        List<Schedule> schedules = scheduleService.findAll();
//
//        for (Schedule schedule : schedules) {
//            ScheduledJob scheduledJob = schedule.getScheduledJob();
//            if (!schedule.getActive() || scheduledJob == null) {
//                continue;
//            }
//            List<Task> tasks = taskService.findByScheduledJobId(scheduledJob.getId());
//            if (tasks.size() == 0) {
//                continue;
//            }
//            Task task = tasks.get(0); // multiple tasks not yet supported; FIXME in a future version
//
//            try {
//                Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(task.getQuartzJobClassName());
//                JobDetail jobDetail = JobBuilder.newJob(jobClass)
//                        .usingJobData("options", task.getOptions())
//                        .build();
//                ScheduleBuilder scheduleBuilder = schedule.getCron() == null ?
//                        SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(10).withRepeatCount(10) :
//                        CronScheduleBuilder.cronSchedule(schedule.getCron());
//                Trigger trigger = TriggerBuilder.newTrigger().startNow().withSchedule(scheduleBuilder).build();
//
//                scheduler.scheduleJob(jobDetail, trigger);
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            } catch (SchedulerException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}