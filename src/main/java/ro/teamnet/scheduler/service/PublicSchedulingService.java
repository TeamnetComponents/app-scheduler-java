package ro.teamnet.scheduler.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.teamnet.scheduler.domain.Configuration;
import ro.teamnet.scheduler.domain.Schedule;
import ro.teamnet.scheduler.domain.ScheduledJob;
import ro.teamnet.scheduler.domain.TimeInterval;
import ro.teamnet.scheduler.dto.ConfigurationDTO;
import ro.teamnet.scheduler.dto.ScheduleDTO;
import ro.teamnet.scheduler.dto.SchedulingBaseDTO;
import ro.teamnet.scheduler.dto.schedule.BasicRecurrentScheduleDTO;
import ro.teamnet.scheduler.dto.schedule.SingleExecutionScheduleDTO;
import ro.teamnet.scheduler.enums.ScheduleType;

import javax.inject.Inject;

@Service
@Transactional
public class PublicSchedulingService implements SchedulingService {
    @Inject
    private ScheduleService scheduleService;

    @Inject
    private TimeIntervalService timeIntervalService;

    @Inject
    private ScheduledJobService scheduledJobService;

    @Inject
    private ConfigurationService configurationService;

    @Override
    public void createJob(ConfigurationDTO configurationDTO) {
        ScheduledJob job = scheduledJobService.save(new ScheduledJob());
        Configuration configuration = new Configuration(configurationDTO);
        configuration.setScheduledJob(job);
        configurationService.save(configuration);
    }

    @Override
    public void deleteJob(ConfigurationDTO configurationDTO) {
        Long baseJobId = configurationService.findBaseJobByConfiguration(configurationDTO).getId();
        configurationService.deleteByConfigurationIdAndType(configurationDTO.getConfigurationId(), configurationDTO.getType());
        scheduledJobService.delete(baseJobId);
    }

    @Override
    public Long scheduleJob(ConfigurationDTO configurationDTO, ScheduleDTO scheduleDTO) {
        ScheduledJob scheduledJob = configurationService.findBaseJobByConfiguration(configurationDTO);
        Long scheduleId = null;

        Schedule schedule = new Schedule();
        schedule.setScheduledJob(scheduledJob);
        schedule.setMisfirePolicy(scheduleDTO.getMisfirePolicy());
        schedule.setActive(true);
        if (scheduleDTO.getScheduleType() == ScheduleType.SINGLE_EXECUTION_SCHEDULE) {
            SingleExecutionScheduleDTO singleExecutionScheduleDTO = (SingleExecutionScheduleDTO) scheduleDTO;
            schedule.setStartTime(singleExecutionScheduleDTO.getExecutionDateTime());
            schedule.setRecurrent(false);
        }
        else if (scheduleDTO.getScheduleType() == ScheduleType.BASIC_RECURRENT_SCHEDULE) {
            BasicRecurrentScheduleDTO basicRecurrentScheduleDTO = (BasicRecurrentScheduleDTO) scheduleDTO;
            schedule.setStartTime(basicRecurrentScheduleDTO.getStartTime());
            schedule.setEndTime(basicRecurrentScheduleDTO.getEndTime());
            schedule.setRepetitions(basicRecurrentScheduleDTO.getRepetitions());
            schedule.setRecurrent(true);
            TimeInterval timeInterval = timeIntervalService.findOneOrCreate(basicRecurrentScheduleDTO.getTimeIntervalDTO());
            schedule.setTimeInterval(timeInterval);
        }
        scheduleService.save(schedule);
        return schedule.getId();
    }

    @Override
    public void unscheduleJob(ConfigurationDTO configurationDTO) {
        ScheduledJob scheduledJob = configurationService.findBaseJobByConfiguration(configurationDTO);
        for (Schedule schedule : scheduleService.findByScheduledJobId(scheduledJob.getId())) {
            scheduleService.delete(schedule.getId());
        }
    }

    @Override
    public void unscheduleJob(ConfigurationDTO configurationDTO, Long scheduleId) {
        scheduleService.delete(scheduleId);
    }

    @Override
    public SchedulingBaseDTO getSchedulingBase(ConfigurationDTO configurationDTO) {

        ScheduledJob job = configurationService.findBaseJobByConfiguration(configurationDTO);

        return new SchedulingBaseDTO(job.getId(), job.getVersion());
    }

    @Override
    public ConfigurationDTO getConfigurationDTOByJobExecId(Long jobExecId) {
        Configuration configuration = configurationService.findByJobExecId(jobExecId);

        return new ConfigurationDTO(configuration.getConfigurationId(), configuration.getType());
    }
}
