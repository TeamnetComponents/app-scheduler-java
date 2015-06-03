package ro.teamnet.scheduler.web.rest;


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
    private static final String DEFAULT_TYPE = "SAMPLE_TEXT";
    private static final String UPDATED_TYPE = "UPDATED_TEXT";
    private static final String DEFAULT_QUARTZ_JOB_CLASS_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_QUARTZ_JOB_CLASS_NAME = "UPDATED_TEXT";

    private static final Long DEFAULT_VERSION = 0L;
    private static final Long UPDATED_VERSION = 1L;

    private static final Boolean DEFAULT_DELETED = false;
    private static final Boolean UPDATED_DELETED = true;

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
        scheduledJob.setType(DEFAULT_TYPE);
        scheduledJob.setQuartzJobClassName(DEFAULT_QUARTZ_JOB_CLASS_NAME);
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
        assertThat(testScheduledJob.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testScheduledJob.getQuartzJobClassName()).isEqualTo(DEFAULT_QUARTZ_JOB_CLASS_NAME);
        assertThat(testScheduledJob.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testScheduledJob.getDeleted()).isEqualTo(DEFAULT_DELETED);
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
                .andExpect(jsonPath("$.[0].type").value(DEFAULT_TYPE.toString()))
                .andExpect(jsonPath("$.[0].quartzJobClassName").value(DEFAULT_QUARTZ_JOB_CLASS_NAME.toString()))
                .andExpect(jsonPath("$.[0].version").value(DEFAULT_VERSION.intValue()))
                .andExpect(jsonPath("$.[0].deleted").value(DEFAULT_DELETED.booleanValue()));
    }

    @Test
    @Transactional
    public void getScheduledJob() throws Exception {
        // Initialize the database
        ScheduledJob scheduledJob = scheduledJobRepository.saveAndFlush(this.scheduledJob);

        // Get the scheduledJob
        restScheduledJobMockMvc.perform(get("/app/rest/scheduledJob/{id}", scheduledJob.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(scheduledJob.getId().intValue()))
                .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
                .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
                .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
                .andExpect(jsonPath("$.quartzJobClassName").value(DEFAULT_QUARTZ_JOB_CLASS_NAME.toString()))
                .andExpect(jsonPath("$.version").value(DEFAULT_VERSION.intValue()))
                .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED.booleanValue()));
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
        ScheduledJob scheduledJob = scheduledJobRepository.saveAndFlush(this.scheduledJob);

        // Update the scheduledJob
        scheduledJob.setName(UPDATED_NAME);
        scheduledJob.setDescription(UPDATED_DESCRIPTION);
        scheduledJob.setType(UPDATED_TYPE);
        scheduledJob.setQuartzJobClassName(UPDATED_QUARTZ_JOB_CLASS_NAME);
        scheduledJob.setDeleted(UPDATED_DELETED);
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
        assertThat(testScheduledJob.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testScheduledJob.getQuartzJobClassName()).isEqualTo(UPDATED_QUARTZ_JOB_CLASS_NAME);
        assertThat(testScheduledJob.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testScheduledJob.getDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    public void deleteScheduledJob() throws Exception {
        // Initialize the database
        ScheduledJob scheduledJob = scheduledJobRepository.saveAndFlush(this.scheduledJob);

        // Get the scheduledJob
        restScheduledJobMockMvc.perform(delete("/app/rest/scheduledJob/{id}", scheduledJob.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<ScheduledJob> scheduledJobs = service.findAll();
        assertThat(scheduledJobs).hasSize(0);
        List<ScheduledJob> scheduledJobsDeleted = scheduledJobRepository.findByDeletedFalse();
        assertThat(scheduledJobsDeleted).hasSize(0);
    }
}
