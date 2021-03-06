package ro.teamnet.scheduler.web.rest;


import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ro.teamnet.SchedulerTestApplication;
import ro.teamnet.scheduler.domain.RecurrentTimeUnit;
import ro.teamnet.scheduler.domain.Schedule;
import ro.teamnet.scheduler.repository.ScheduleRepository;
import ro.teamnet.scheduler.service.ScheduleService;
import ro.teamnet.web.rest.TestUtil;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ScheduleResource REST controller.
 *
 * @see ScheduleResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SchedulerTestApplication.class)
@WebAppConfiguration
@IntegrationTest
@ActiveProfiles("test-scheduler")
public class ScheduleResourceTest {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");


    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final Boolean DEFAULT_RECURRENT = false;
    private static final Boolean UPDATED_RECURRENT = true;

    private static final DateTime DEFAULT_START_TIME = new DateTime(0L);
    private static final DateTime UPDATED_START_TIME = new DateTime().withMillisOfSecond(0);
    private static final String DEFAULT_START_TIME_STR = DATE_TIME_FORMATTER.print(DEFAULT_START_TIME);

    private static final DateTime DEFAULT_END_TIME = new DateTime(0L);
    private static final DateTime UPDATED_END_TIME = new DateTime().withMillisOfSecond(0);
    private static final String DEFAULT_END_TIME_STR = DATE_TIME_FORMATTER.print(DEFAULT_END_TIME);

    private static final Long DEFAULT_REPETITIONS = 0L;
    private static final Long UPDATED_REPETITIONS = 1L;

    private static final Long DEFAULT_VERSION = 0L;
    private static final Long UPDATED_VERSION = 1L;

    private static final Boolean DEFAULT_DELETED = false;
    private static final Boolean UPDATED_DELETED = true;

    @Inject
    private ScheduleRepository scheduleRepository;

    @Inject
    private ScheduleService service;

    private MockMvc restScheduleMockMvc;

    private Schedule schedule;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ScheduleResource scheduleResource = new ScheduleResource(service);
        this.restScheduleMockMvc = MockMvcBuilders.standaloneSetup(scheduleResource).build();
    }

    @Before
    public void initTest() {
        schedule = new Schedule();
        schedule.setActive(DEFAULT_ACTIVE);
        schedule.setRecurrent(DEFAULT_RECURRENT);
        schedule.setStartTime(DEFAULT_START_TIME);
        schedule.setEndTime(DEFAULT_END_TIME);
        schedule.setRepetitions(DEFAULT_REPETITIONS);
    }

    @Test
    @Transactional
    public void createSchedule() throws Exception {
        // Validate the database is empty
        assertThat(scheduleRepository.findAll()).hasSize(0);

        // Create the Schedule
        restScheduleMockMvc.perform(post("/app/rest/schedule")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(schedule)))
                .andExpect(status().isOk());

        // Validate the Schedule in the database
        List<Schedule> schedules = scheduleRepository.findAll();
        assertThat(schedules).hasSize(1);
        Schedule testSchedule = schedules.iterator().next();
        assertThat(testSchedule.getActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testSchedule.getRecurrent()).isEqualTo(DEFAULT_RECURRENT);
        assertThat(testSchedule.getCron()).endsWith("1 1 ? 1970");
        assertThat(testSchedule.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testSchedule.getEndTime()).isEqualTo(DEFAULT_END_TIME);
        assertThat(testSchedule.getRepetitions()).isEqualTo(DEFAULT_REPETITIONS);
        assertThat(testSchedule.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testSchedule.getDeleted()).isEqualTo(DEFAULT_DELETED);
    }

    @Test
    @Transactional
    public void createScheduleWithRecurrentTimeUnits() throws Exception {
        // Validate the database is empty
        assertThat(scheduleRepository.findAll()).hasSize(0);
        Schedule schedule = this.schedule;
        add3RecurrentTimeUnits(schedule);

        // Create the Schedule
        restScheduleMockMvc.perform(post("/app/rest/schedule")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(schedule)))
                .andExpect(status().isOk());

        // Validate the Schedule in the database
        List<Schedule> schedules = scheduleRepository.findAll();
        assertThat(schedules).hasSize(1);
        Schedule testSchedule = schedules.iterator().next();
        assertThat(testSchedule.getActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testSchedule.getRecurrent()).isEqualTo(DEFAULT_RECURRENT);
        assertThat(testSchedule.getCron()).endsWith("1 1 ? 1970");
        assertThat(testSchedule.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testSchedule.getEndTime()).isEqualTo(DEFAULT_END_TIME);
        assertThat(testSchedule.getRepetitions()).isEqualTo(DEFAULT_REPETITIONS);
        assertThat(testSchedule.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testSchedule.getDeleted()).isEqualTo(DEFAULT_DELETED);
        assertThat(testSchedule.getRecurrentTimeUnits().size()).isEqualTo(3);
    }

    private void add3RecurrentTimeUnits(Schedule schedule) {
        HashSet<RecurrentTimeUnit> recurrentTimeUnits = new HashSet<>();
        RecurrentTimeUnit rtu1 = new RecurrentTimeUnit();
        rtu1.setValue(1);
        recurrentTimeUnits.add(rtu1);
        RecurrentTimeUnit rtu2 = new RecurrentTimeUnit();
        rtu2.setValue(2);
        recurrentTimeUnits.add(rtu2);
        RecurrentTimeUnit rtu3 = new RecurrentTimeUnit();
        rtu3.setValue(3);
        recurrentTimeUnits.add(rtu3);
        schedule.setRecurrentTimeUnits(recurrentTimeUnits);
    }

    @Test
    @Transactional
    public void getAllSchedules() throws Exception {
        // Initialize the database
        service.save(schedule);

        // Get all the schedules
        restScheduleMockMvc.perform(get("/app/rest/schedule"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].active").value(DEFAULT_ACTIVE))
                .andExpect(jsonPath("$.[0].recurrent").value(DEFAULT_RECURRENT))
                .andExpect(jsonPath("$.[0].cron", Matchers.endsWith("1 1 ? 1970")))
                .andExpect(jsonPath("$.[0].startTime").value(DEFAULT_START_TIME_STR))
                .andExpect(jsonPath("$.[0].endTime").value(DEFAULT_END_TIME_STR))
                .andExpect(jsonPath("$.[0].repetitions").value(DEFAULT_REPETITIONS.intValue()))
                .andExpect(jsonPath("$.[0].version").value(DEFAULT_VERSION.intValue()))
                .andExpect(jsonPath("$.[0].deleted").value(DEFAULT_DELETED));
    }

    @Test
    @Transactional
    public void getSchedule() throws Exception {
        // Initialize the database
        Schedule initialSchedule = service.save(this.schedule);

        // Get the schedule
        restScheduleMockMvc.perform(get("/app/rest/schedule/{id}", initialSchedule.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(initialSchedule.getId().intValue()))
                .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE))
                .andExpect(jsonPath("$.recurrent").value(DEFAULT_RECURRENT))
                .andExpect(jsonPath("$.cron", Matchers.endsWith("1 1 ? 1970")))
                .andExpect(jsonPath("$.startTime").value(DEFAULT_START_TIME_STR))
                .andExpect(jsonPath("$.endTime").value(DEFAULT_END_TIME_STR))
                .andExpect(jsonPath("$.repetitions").value(DEFAULT_REPETITIONS.intValue()))
                .andExpect(jsonPath("$.version").value(DEFAULT_VERSION.intValue()))
                .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED));
    }

    @Test
    @Transactional
    public void getNonExistingSchedule() throws Exception {
        // Get the schedule
        restScheduleMockMvc.perform(get("/app/rest/schedule/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateSchedule() throws Exception {
        // Initialize the database
        Schedule initialSchedule = scheduleRepository.saveAndFlush(this.schedule);
        String initialCron = initialSchedule.getCron();

        // Update the schedule
        initialSchedule.setActive(UPDATED_ACTIVE);
        initialSchedule.setRecurrent(UPDATED_RECURRENT);
        initialSchedule.setStartTime(UPDATED_START_TIME);
        initialSchedule.setEndTime(UPDATED_END_TIME);
        initialSchedule.setRepetitions(UPDATED_REPETITIONS);

        initialSchedule.setDeleted(UPDATED_DELETED);
        restScheduleMockMvc.perform(post("/app/rest/schedule")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(initialSchedule)))
                .andExpect(status().isOk());

        // Validate the Schedule in the database
        List<Schedule> schedules = scheduleRepository.findAll();
        assertThat(schedules).hasSize(1);
        Schedule testSchedule = schedules.iterator().next();
        assertThat(testSchedule.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testSchedule.getRecurrent()).isEqualTo(UPDATED_RECURRENT);
        assertThat(testSchedule.getCron()).isNotNull();
        assertThat(testSchedule.getCron()).isNotEqualTo(initialCron);
        assertThat(testSchedule.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testSchedule.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testSchedule.getRepetitions()).isEqualTo(UPDATED_REPETITIONS);
        assertThat(testSchedule.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testSchedule.getDeleted()).isEqualTo(UPDATED_DELETED);

        scheduleRepository.delete(testSchedule);
    }

    @Test
    public void updateScheduleWithRecurrentTimeUnits() throws Exception {
        // Initialize the database
        add3RecurrentTimeUnits(schedule);
        Schedule initialSchedule = scheduleRepository.saveAndFlush(this.schedule);
        assertThat(initialSchedule.getRecurrentTimeUnits().size()).isEqualTo(3);
        String initialCron = initialSchedule.getCron();

        // Update the schedule
        initialSchedule.setActive(UPDATED_ACTIVE);
        initialSchedule.setRecurrent(UPDATED_RECURRENT);
        initialSchedule.setStartTime(UPDATED_START_TIME);
        initialSchedule.setEndTime(UPDATED_END_TIME);
        initialSchedule.setRepetitions(UPDATED_REPETITIONS);

        RecurrentTimeUnit recurrentTimeUnit = new RecurrentTimeUnit();
        recurrentTimeUnit.setValue(3);

        Set<RecurrentTimeUnit> recurrentTimeUnits = new HashSet<>();
        recurrentTimeUnits.add(recurrentTimeUnit);
        recurrentTimeUnits.add(initialSchedule.getRecurrentTimeUnits().iterator().next());
        initialSchedule.setRecurrentTimeUnits(recurrentTimeUnits);

        initialSchedule.setDeleted(UPDATED_DELETED);
        restScheduleMockMvc.perform(post("/app/rest/schedule")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(initialSchedule)))
                .andExpect(status().isOk());

        // Validate the Schedule in the database
        List<Schedule> schedules = scheduleRepository.findAll();
        assertThat(schedules).hasSize(1);
        Schedule testSchedule = schedules.iterator().next();
        assertThat(testSchedule.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testSchedule.getRecurrent()).isEqualTo(UPDATED_RECURRENT);
        assertThat(testSchedule.getCron()).isNotNull();
        assertThat(testSchedule.getCron()).isNotEqualTo(initialCron);
        assertThat(testSchedule.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testSchedule.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testSchedule.getRepetitions()).isEqualTo(UPDATED_REPETITIONS);
        assertThat(testSchedule.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testSchedule.getDeleted()).isEqualTo(UPDATED_DELETED);
        assertThat(testSchedule.getRecurrentTimeUnits().size()).isEqualTo(2);

        scheduleRepository.delete(testSchedule);
    }

    @Test
    @Transactional
    public void deleteSchedule() throws Exception {
        // Initialize the database
        Schedule initialSchedule = scheduleRepository.saveAndFlush(this.schedule);

        // Get the schedule
        restScheduleMockMvc.perform(delete("/app/rest/schedule/{id}", initialSchedule.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Schedule> schedules = service.findAll();
        assertThat(schedules).hasSize(0);
        List<Schedule> schedulesNotDeleted = scheduleRepository.findByDeletedFalse();
        assertThat(schedulesNotDeleted).hasSize(0);
    }
}
