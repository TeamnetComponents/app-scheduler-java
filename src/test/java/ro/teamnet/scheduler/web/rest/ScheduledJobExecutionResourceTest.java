
package ro.teamnet.scheduler.web.rest;


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
import ro.teamnet.scheduler.domain.ScheduledJobExecution;
import ro.teamnet.scheduler.job.JobExecutionStatus;
import ro.teamnet.scheduler.repository.ScheduledJobExecutionRepository;
import ro.teamnet.scheduler.service.ScheduledJobExecutionService;
import ro.teamnet.web.rest.TestUtil;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ScheduledJobExecutionResource REST controller.
 *
 * @see ScheduledJobExecutionResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SchedulerTestApplication.class)
@WebAppConfiguration
@IntegrationTest
@ActiveProfiles("test-scheduler")
public class ScheduledJobExecutionResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");


    private static final DateTime DEFAULT_SCHEDULED_FIRE_TIME = new DateTime(0L);
    private static final DateTime UPDATED_SCHEDULED_FIRE_TIME = new DateTime().withMillisOfSecond(0);
    private static final String DEFAULT_SCHEDULED_FIRE_TIME_STR = dateTimeFormatter.print(DEFAULT_SCHEDULED_FIRE_TIME);

    private static final DateTime DEFAULT_ACTUAL_FIRE_TIME = new DateTime(0L);
    private static final DateTime UPDATED_ACTUAL_FIRE_TIME = new DateTime().withMillisOfSecond(0);
    private static final String DEFAULT_ACTUAL_FIRE_TIME_STR = dateTimeFormatter.print(DEFAULT_ACTUAL_FIRE_TIME);

    private static final DateTime DEFAULT_LAST_FIRE_TIME = new DateTime(0L);
    private static final DateTime UPDATED_LAST_FIRE_TIME = new DateTime().withMillisOfSecond(0);
    private static final String DEFAULT_LAST_FIRE_TIME_STR = dateTimeFormatter.print(DEFAULT_LAST_FIRE_TIME);

    private static final DateTime DEFAULT_NEXT_FIRE_TIME = new DateTime(0L);
    private static final DateTime UPDATED_NEXT_FIRE_TIME = new DateTime().withMillisOfSecond(0);
    private static final String DEFAULT_NEXT_FIRE_TIME_STR = dateTimeFormatter.print(DEFAULT_NEXT_FIRE_TIME);
    private static final String DEFAULT_STATE = "SAMPLE_TEXT";
    private static final String UPDATED_STATE = "UPDATED_TEXT";
    private static final JobExecutionStatus DEFAULT_STATUS = JobExecutionStatus.RUNNING;
    private static final JobExecutionStatus UPDATED_STATUS = JobExecutionStatus.FINISHED;

    @Inject
    private ScheduledJobExecutionRepository scheduledJobExecutionRepository;

    @Inject
    ScheduledJobExecutionService service;

    private MockMvc restScheduledJobExecutionMockMvc;

    private ScheduledJobExecution scheduledJobExecution;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ScheduledJobExecutionResource scheduledJobExecutionResource = new ScheduledJobExecutionResource(service);
        this.restScheduledJobExecutionMockMvc = MockMvcBuilders.standaloneSetup(scheduledJobExecutionResource).build();
    }

    @Before
    public void initTest() {
        scheduledJobExecution = new ScheduledJobExecution();
        scheduledJobExecution.setScheduledFireTime(DEFAULT_SCHEDULED_FIRE_TIME);
        scheduledJobExecution.setActualFireTime(DEFAULT_ACTUAL_FIRE_TIME);
        scheduledJobExecution.setLastFireTime(DEFAULT_LAST_FIRE_TIME);
        scheduledJobExecution.setNextFireTime(DEFAULT_NEXT_FIRE_TIME);
        scheduledJobExecution.setState(DEFAULT_STATE);
        scheduledJobExecution.setStatus(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    public void createScheduledJobExecution() throws Exception {
        // Validate the database is empty
        assertThat(scheduledJobExecutionRepository.findAll()).hasSize(0);

        // Create the ScheduledJobExecution
        restScheduledJobExecutionMockMvc.perform(post("/app/rest/scheduledJobExecution")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(scheduledJobExecution)))
                .andExpect(status().isOk());

        // Validate the ScheduledJobExecution in the database
        List<ScheduledJobExecution> scheduledJobExecutions = scheduledJobExecutionRepository.findAll();
        assertThat(scheduledJobExecutions).hasSize(1);
        ScheduledJobExecution testScheduledJobExecution = scheduledJobExecutions.iterator().next();
        assertThat(testScheduledJobExecution.getScheduledFireTime()).isEqualTo(DEFAULT_SCHEDULED_FIRE_TIME);
        assertThat(testScheduledJobExecution.getActualFireTime()).isEqualTo(DEFAULT_ACTUAL_FIRE_TIME);
        assertThat(testScheduledJobExecution.getLastFireTime()).isEqualTo(DEFAULT_LAST_FIRE_TIME);
        assertThat(testScheduledJobExecution.getNextFireTime()).isEqualTo(DEFAULT_NEXT_FIRE_TIME);
        assertThat(testScheduledJobExecution.getState()).isEqualTo(DEFAULT_STATE);
        assertThat(testScheduledJobExecution.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    public void getAllScheduledJobExecutions() throws Exception {
        // Initialize the database
        scheduledJobExecutionRepository.saveAndFlush(scheduledJobExecution);

        // Get all the scheduledJobExecutions
        restScheduledJobExecutionMockMvc.perform(get("/app/rest/scheduledJobExecution"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].scheduledFireTime").value(DEFAULT_SCHEDULED_FIRE_TIME_STR))
                .andExpect(jsonPath("$.[0].actualFireTime").value(DEFAULT_ACTUAL_FIRE_TIME_STR))
                .andExpect(jsonPath("$.[0].lastFireTime").value(DEFAULT_LAST_FIRE_TIME_STR))
                .andExpect(jsonPath("$.[0].nextFireTime").value(DEFAULT_NEXT_FIRE_TIME_STR))
                .andExpect(jsonPath("$.[0].state").value(DEFAULT_STATE.toString()))
                .andExpect(jsonPath("$.[0].status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    public void getScheduledJobExecution() throws Exception {
        // Initialize the database
        scheduledJobExecutionRepository.saveAndFlush(scheduledJobExecution);

        // Get the scheduledJobExecution
        restScheduledJobExecutionMockMvc.perform(get("/app/rest/scheduledJobExecution/{id}", scheduledJobExecution.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(scheduledJobExecution.getId().intValue()))
            .andExpect(jsonPath("$.scheduledFireTime").value(DEFAULT_SCHEDULED_FIRE_TIME_STR))
            .andExpect(jsonPath("$.actualFireTime").value(DEFAULT_ACTUAL_FIRE_TIME_STR))
            .andExpect(jsonPath("$.lastFireTime").value(DEFAULT_LAST_FIRE_TIME_STR))
            .andExpect(jsonPath("$.nextFireTime").value(DEFAULT_NEXT_FIRE_TIME_STR))
            .andExpect(jsonPath("$.state").value(DEFAULT_STATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingScheduledJobExecution() throws Exception {
        // Get the scheduledJobExecution
        restScheduledJobExecutionMockMvc.perform(get("/app/rest/scheduledJobExecution/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateScheduledJobExecution() throws Exception {
        // Initialize the database
        scheduledJobExecutionRepository.saveAndFlush(scheduledJobExecution);

        // Update the scheduledJobExecution
        scheduledJobExecution.setScheduledFireTime(UPDATED_SCHEDULED_FIRE_TIME);
        scheduledJobExecution.setActualFireTime(UPDATED_ACTUAL_FIRE_TIME);
        scheduledJobExecution.setLastFireTime(UPDATED_LAST_FIRE_TIME);
        scheduledJobExecution.setNextFireTime(UPDATED_NEXT_FIRE_TIME);
        scheduledJobExecution.setState(UPDATED_STATE);
        scheduledJobExecution.setStatus(UPDATED_STATUS);
        restScheduledJobExecutionMockMvc.perform(post("/app/rest/scheduledJobExecution")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(scheduledJobExecution)))
                .andExpect(status().isOk());

        // Validate the ScheduledJobExecution in the database
        List<ScheduledJobExecution> scheduledJobExecutions = scheduledJobExecutionRepository.findAll();
        assertThat(scheduledJobExecutions).hasSize(1);
        ScheduledJobExecution testScheduledJobExecution = scheduledJobExecutions.iterator().next();
        assertThat(testScheduledJobExecution.getScheduledFireTime()).isEqualTo(UPDATED_SCHEDULED_FIRE_TIME);
        assertThat(testScheduledJobExecution.getActualFireTime()).isEqualTo(UPDATED_ACTUAL_FIRE_TIME);
        assertThat(testScheduledJobExecution.getLastFireTime()).isEqualTo(UPDATED_LAST_FIRE_TIME);
        assertThat(testScheduledJobExecution.getNextFireTime()).isEqualTo(UPDATED_NEXT_FIRE_TIME);
        assertThat(testScheduledJobExecution.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testScheduledJobExecution.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void deleteScheduledJobExecution() throws Exception {
        // Initialize the database
        scheduledJobExecutionRepository.saveAndFlush(scheduledJobExecution);

        // Get the scheduledJobExecution
        restScheduledJobExecutionMockMvc.perform(delete("/app/rest/scheduledJobExecution/{id}", scheduledJobExecution.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<ScheduledJobExecution> scheduledJobExecutions = scheduledJobExecutionRepository.findAll();
        assertThat(scheduledJobExecutions).hasSize(0);
    }
}
