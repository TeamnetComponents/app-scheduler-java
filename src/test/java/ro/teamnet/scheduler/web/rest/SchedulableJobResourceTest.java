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
import ro.teamnet.scheduler.domain.SchedulableJob;
import ro.teamnet.scheduler.repository.SchedulableJobRepository;
import ro.teamnet.scheduler.service.SchedulableJobService;
import ro.teamnet.web.rest.TestUtil;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the SchedulableJobResource REST controller.
 *
 * @see SchedulableJobResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SchedulerTestApplication.class)
@WebAppConfiguration
@IntegrationTest
@ActiveProfiles("test-scheduler")
public class SchedulableJobResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_OPTIONS = "SAMPLE_TEXT";
    private static final String UPDATED_OPTIONS = "UPDATED_TEXT";

    private static final DateTime DEFAULT_NEXT_SCHEDULED_EXECUTION = new DateTime(0L);
    private static final DateTime UPDATED_NEXT_SCHEDULED_EXECUTION = new DateTime().withMillisOfSecond(0);
    private static final String DEFAULT_NEXT_SCHEDULED_EXECUTION_STR = dateTimeFormatter.print(DEFAULT_NEXT_SCHEDULED_EXECUTION);

    private static final DateTime DEFAULT_LAST_EXECUTION_TIME = new DateTime(0L);
    private static final DateTime UPDATED_LAST_EXECUTION_TIME = new DateTime().withMillisOfSecond(0);
    private static final String DEFAULT_LAST_EXECUTION_TIME_STR = dateTimeFormatter.print(DEFAULT_LAST_EXECUTION_TIME);
    private static final String DEFAULT_LAST_EXECUTION_STATE = "SAMPLE_TEXT";
    private static final String UPDATED_LAST_EXECUTION_STATE = "UPDATED_TEXT";

    @Inject
    private SchedulableJobRepository schedulableJobRepository;

    @Inject
    SchedulableJobService service;

    private MockMvc restSchedulableJobMockMvc;

    private SchedulableJob schedulableJob;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SchedulableJobResource schedulableJobResource = new SchedulableJobResource(service);
        this.restSchedulableJobMockMvc = MockMvcBuilders.standaloneSetup(schedulableJobResource).build();
    }

    @Before
    public void initTest() {
        schedulableJob = new SchedulableJob();
        schedulableJob.setName(DEFAULT_NAME);
        schedulableJob.setOptions(DEFAULT_OPTIONS);
        schedulableJob.setNextScheduledExecution(DEFAULT_NEXT_SCHEDULED_EXECUTION);
        schedulableJob.setLastExecutionTime(DEFAULT_LAST_EXECUTION_TIME);
        schedulableJob.setLastExecutionState(DEFAULT_LAST_EXECUTION_STATE);
    }

    @Test
    @Transactional
    public void createSchedulableJob() throws Exception {
        // Validate the database is empty
        assertThat(schedulableJobRepository.findAll()).hasSize(0);

        // Create the SchedulableJob
        restSchedulableJobMockMvc.perform(post("/app/rest/schedulableJob")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(schedulableJob)))
                .andExpect(status().isOk());

        // Validate the SchedulableJob in the database
        List<SchedulableJob> schedulableJobs = schedulableJobRepository.findAll();
        assertThat(schedulableJobs).hasSize(1);
        SchedulableJob testSchedulableJob = schedulableJobs.iterator().next();
        assertThat(testSchedulableJob.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSchedulableJob.getOptions()).isEqualTo(DEFAULT_OPTIONS);
        assertThat(testSchedulableJob.getNextScheduledExecution()).isEqualTo(DEFAULT_NEXT_SCHEDULED_EXECUTION);
        assertThat(testSchedulableJob.getLastExecutionTime()).isEqualTo(DEFAULT_LAST_EXECUTION_TIME);
        assertThat(testSchedulableJob.getLastExecutionState()).isEqualTo(DEFAULT_LAST_EXECUTION_STATE);
    }

    @Test
    @Transactional
    public void getAllSchedulableJobs() throws Exception {
        // Initialize the database
        schedulableJobRepository.saveAndFlush(schedulableJob);

        // Get all the schedulableJobs
        restSchedulableJobMockMvc.perform(get("/app/rest/schedulableJob"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].name").value(DEFAULT_NAME.toString()))
                .andExpect(jsonPath("$.[0].options").value(DEFAULT_OPTIONS.toString()))
                .andExpect(jsonPath("$.[0].nextScheduledExecution").value(DEFAULT_NEXT_SCHEDULED_EXECUTION_STR))
                .andExpect(jsonPath("$.[0].lastExecutionTime").value(DEFAULT_LAST_EXECUTION_TIME_STR))
                .andExpect(jsonPath("$.[0].lastExecutionState").value(DEFAULT_LAST_EXECUTION_STATE.toString()));
    }

    @Test
    @Transactional
    public void getSchedulableJob() throws Exception {
        // Initialize the database
        schedulableJobRepository.saveAndFlush(schedulableJob);

        // Get the schedulableJob
        restSchedulableJobMockMvc.perform(get("/app/rest/schedulableJob/{id}", schedulableJob.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(schedulableJob.getId().intValue()))
                .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
                .andExpect(jsonPath("$.options").value(DEFAULT_OPTIONS.toString()))
                .andExpect(jsonPath("$.nextScheduledExecution").value(DEFAULT_NEXT_SCHEDULED_EXECUTION_STR))
                .andExpect(jsonPath("$.lastExecutionTime").value(DEFAULT_LAST_EXECUTION_TIME_STR))
                .andExpect(jsonPath("$.lastExecutionState").value(DEFAULT_LAST_EXECUTION_STATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSchedulableJob() throws Exception {
        // Get the schedulableJob
        restSchedulableJobMockMvc.perform(get("/app/rest/schedulableJob/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSchedulableJob() throws Exception {
        // Initialize the database
        schedulableJobRepository.saveAndFlush(schedulableJob);

        // Update the schedulableJob
        schedulableJob.setName(UPDATED_NAME);
        schedulableJob.setOptions(UPDATED_OPTIONS);
        schedulableJob.setNextScheduledExecution(UPDATED_NEXT_SCHEDULED_EXECUTION);
        schedulableJob.setLastExecutionTime(UPDATED_LAST_EXECUTION_TIME);
        schedulableJob.setLastExecutionState(UPDATED_LAST_EXECUTION_STATE);
        restSchedulableJobMockMvc.perform(post("/app/rest/schedulableJob")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(schedulableJob)))
                .andExpect(status().isOk());

        // Validate the SchedulableJob in the database
        List<SchedulableJob> schedulableJobs = schedulableJobRepository.findAll();
        assertThat(schedulableJobs).hasSize(1);
        SchedulableJob testSchedulableJob = schedulableJobs.iterator().next();
        assertThat(testSchedulableJob.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSchedulableJob.getOptions()).isEqualTo(UPDATED_OPTIONS);
        assertThat(testSchedulableJob.getNextScheduledExecution()).isEqualTo(UPDATED_NEXT_SCHEDULED_EXECUTION);
        assertThat(testSchedulableJob.getLastExecutionTime()).isEqualTo(UPDATED_LAST_EXECUTION_TIME);
        assertThat(testSchedulableJob.getLastExecutionState()).isEqualTo(UPDATED_LAST_EXECUTION_STATE);
    }

    @Test
    @Transactional
    public void deleteSchedulableJob() throws Exception {
        // Initialize the database
        schedulableJobRepository.saveAndFlush(schedulableJob);

        // Get the schedulableJob
        restSchedulableJobMockMvc.perform(delete("/app/rest/schedulableJob/{id}", schedulableJob.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<SchedulableJob> schedulableJobs = schedulableJobRepository.findAll();
        assertThat(schedulableJobs).hasSize(0);
    }
}
