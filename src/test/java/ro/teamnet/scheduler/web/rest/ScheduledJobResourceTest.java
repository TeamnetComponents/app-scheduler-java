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
import ro.teamnet.scheduler.domain.ScheduledJob;
import ro.teamnet.scheduler.repository.ScheduledJobRepository;
import ro.teamnet.scheduler.service.ScheduledJobService;
import ro.teamnet.web.rest.TestUtil;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ScheduledJobResource REST controller.
 *
 * @see ScheduledJobResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SchedulerTestApplication.class)
@WebAppConfiguration
@IntegrationTest
@ActiveProfiles("test-scheduler")
public class ScheduledJobResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";
    private static final String UPDATED_DESCRIPTION = "UPDATED_TEXT";

    private static final DateTime DEFAULT_NEXT_SCHEDULED_EXECUTION = new DateTime(0L);
    private static final DateTime UPDATED_NEXT_SCHEDULED_EXECUTION = new DateTime().withMillisOfSecond(0);
    private static final String DEFAULT_NEXT_SCHEDULED_EXECUTION_STR = dateTimeFormatter.print(DEFAULT_NEXT_SCHEDULED_EXECUTION);

    private static final DateTime DEFAULT_LAST_EXECUTION_TIME = new DateTime(0L);
    private static final DateTime UPDATED_LAST_EXECUTION_TIME = new DateTime().withMillisOfSecond(0);
    private static final String DEFAULT_LAST_EXECUTION_TIME_STR = dateTimeFormatter.print(DEFAULT_LAST_EXECUTION_TIME);
    private static final String DEFAULT_LAST_EXECUTION_STATE = "SAMPLE_TEXT";
    private static final String UPDATED_LAST_EXECUTION_STATE = "UPDATED_TEXT";

    @Inject
    private ScheduledJobRepository scheduledJobRepository;

    @Inject
    ScheduledJobService service;

    private MockMvc restScheduledJobMockMvc;

    private ScheduledJob scheduledJob;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ScheduledJobResource scheduledJobResource = new ScheduledJobResource(service);
        this.restScheduledJobMockMvc = MockMvcBuilders.standaloneSetup(scheduledJobResource).build();
    }

    @Before
    public void initTest() {
        scheduledJob = new ScheduledJob();
        scheduledJob.setName(DEFAULT_NAME);
        scheduledJob.setDescription(DEFAULT_DESCRIPTION);
        scheduledJob.setNextScheduledExecution(DEFAULT_NEXT_SCHEDULED_EXECUTION);
        scheduledJob.setLastExecutionTime(DEFAULT_LAST_EXECUTION_TIME);
        scheduledJob.setLastExecutionState(DEFAULT_LAST_EXECUTION_STATE);
    }

    @Test
    @Transactional
    public void createScheduledJob() throws Exception {
        // Validate the database is empty
        assertThat(scheduledJobRepository.findAll()).hasSize(0);

        // Create the ScheduledJob
        restScheduledJobMockMvc.perform(post("/app/rest/scheduledJob")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(scheduledJob)))
                .andExpect(status().isOk());

        // Validate the ScheduledJob in the database
        List<ScheduledJob> scheduledJobs = scheduledJobRepository.findAll();
        assertThat(scheduledJobs).hasSize(1);
        ScheduledJob testScheduledJob = scheduledJobs.iterator().next();
        assertThat(testScheduledJob.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testScheduledJob.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testScheduledJob.getNextScheduledExecution()).isEqualTo(DEFAULT_NEXT_SCHEDULED_EXECUTION);
        assertThat(testScheduledJob.getLastExecutionTime()).isEqualTo(DEFAULT_LAST_EXECUTION_TIME);
        assertThat(testScheduledJob.getLastExecutionState()).isEqualTo(DEFAULT_LAST_EXECUTION_STATE);
    }

    @Test
    @Transactional
    public void getAllScheduledJobs() throws Exception {
        // Initialize the database
        scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobs
        restScheduledJobMockMvc.perform(get("/app/rest/scheduledJob"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].name").value(DEFAULT_NAME.toString()))
                .andExpect(jsonPath("$.[0].description").value(DEFAULT_DESCRIPTION.toString()))
                .andExpect(jsonPath("$.[0].nextScheduledExecution").value(DEFAULT_NEXT_SCHEDULED_EXECUTION_STR))
                .andExpect(jsonPath("$.[0].lastExecutionTime").value(DEFAULT_LAST_EXECUTION_TIME_STR))
                .andExpect(jsonPath("$.[0].lastExecutionState").value(DEFAULT_LAST_EXECUTION_STATE.toString()));
    }

    @Test
    @Transactional
    public void getScheduledJob() throws Exception {
        // Initialize the database
        scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get the scheduledJob
        restScheduledJobMockMvc.perform(get("/app/rest/scheduledJob/{id}", scheduledJob.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(scheduledJob.getId().intValue()))
                .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
                .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
                .andExpect(jsonPath("$.nextScheduledExecution").value(DEFAULT_NEXT_SCHEDULED_EXECUTION_STR))
                .andExpect(jsonPath("$.lastExecutionTime").value(DEFAULT_LAST_EXECUTION_TIME_STR))
                .andExpect(jsonPath("$.lastExecutionState").value(DEFAULT_LAST_EXECUTION_STATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingScheduledJob() throws Exception {
        // Get the scheduledJob
        restScheduledJobMockMvc.perform(get("/app/rest/scheduledJob/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateScheduledJob() throws Exception {
        // Initialize the database
        scheduledJobRepository.saveAndFlush(scheduledJob);

        // Update the scheduledJob
        scheduledJob.setName(UPDATED_NAME);
        scheduledJob.setDescription(UPDATED_DESCRIPTION);
        scheduledJob.setNextScheduledExecution(UPDATED_NEXT_SCHEDULED_EXECUTION);
        scheduledJob.setLastExecutionTime(UPDATED_LAST_EXECUTION_TIME);
        scheduledJob.setLastExecutionState(UPDATED_LAST_EXECUTION_STATE);
        restScheduledJobMockMvc.perform(post("/app/rest/scheduledJob")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(scheduledJob)))
                .andExpect(status().isOk());

        // Validate the ScheduledJob in the database
        List<ScheduledJob> scheduledJobs = scheduledJobRepository.findAll();
        assertThat(scheduledJobs).hasSize(1);
        ScheduledJob testScheduledJob = scheduledJobs.iterator().next();
        assertThat(testScheduledJob.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testScheduledJob.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testScheduledJob.getNextScheduledExecution()).isEqualTo(UPDATED_NEXT_SCHEDULED_EXECUTION);
        assertThat(testScheduledJob.getLastExecutionTime()).isEqualTo(UPDATED_LAST_EXECUTION_TIME);
        assertThat(testScheduledJob.getLastExecutionState()).isEqualTo(UPDATED_LAST_EXECUTION_STATE);
    }

    @Test
    @Transactional
    public void deleteScheduledJob() throws Exception {
        // Initialize the database
        scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get the scheduledJob
        restScheduledJobMockMvc.perform(delete("/app/rest/scheduledJob/{id}", scheduledJob.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<ScheduledJob> scheduledJobs = scheduledJobRepository.findAll();
        assertThat(scheduledJobs).hasSize(0);
    }
}
