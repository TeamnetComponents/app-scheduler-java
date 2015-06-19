package ro.teamnet.scheduler.web.rest;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SchedulerTestApplication.class)
@WebAppConfiguration
@IntegrationTest
@ActiveProfiles("test-scheduler")
public class ScheduleResourceUpdateTest {

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final Boolean DEFAULT_RECURRENT = false;
    private static final Boolean UPDATED_RECURRENT = true;

    private static final DateTime DEFAULT_START_TIME = new DateTime(0L);
    private static final DateTime UPDATED_START_TIME = new DateTime().withMillisOfSecond(0);

    private static final DateTime DEFAULT_END_TIME = new DateTime(0L);
    private static final DateTime UPDATED_END_TIME = new DateTime().withMillisOfSecond(0);

    private static final Long DEFAULT_REPETITIONS = 0L;
    private static final Long UPDATED_REPETITIONS = 1L;

    private static final Long UPDATED_VERSION = 1L;

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
    public void updateScheduleWithRecurrentTimeUnits() throws Exception {

        // Initialize the database
        Schedule initialSchedule = this.schedule;
        add3RecurrentTimeUnits(initialSchedule);
        initialSchedule = scheduleRepository.saveAndFlush(initialSchedule);
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
        assertThat(schedules).hasSize(2);
        Schedule testSchedule = schedules.get(schedules.size() - 1);
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
    }
}
