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
import ro.teamnet.scheduler.domain.TimeInterval;
import ro.teamnet.scheduler.repository.TimeIntervalRepository;
import ro.teamnet.scheduler.service.TimeIntervalService;
import ro.teamnet.web.rest.TestUtil;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the TimeIntervalResource REST controller.
 *
 * @see TimeIntervalResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SchedulerTestApplication.class)
@WebAppConfiguration
@IntegrationTest
@ActiveProfiles("test-scheduler")
public class TimeIntervalResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";

    private static final Boolean DEFAULT_CUSTOM = false;
    private static final Boolean UPDATED_CUSTOM = true;

    private static final Long DEFAULT_INTERVAL_MILLIS = 0L;
    private static final Long UPDATED_INTERVAL_MILLIS = 1L;

    private static final Long DEFAULT_INTERVAL = 0L;
    private static final Long UPDATED_INTERVAL = 1L;

    @Inject
    private TimeIntervalRepository timeIntervalRepository;

    @Inject
    TimeIntervalService service;

    private MockMvc restTimeIntervalMockMvc;

    private TimeInterval timeInterval;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TimeIntervalResource timeIntervalResource = new TimeIntervalResource(service);
        this.restTimeIntervalMockMvc = MockMvcBuilders.standaloneSetup(timeIntervalResource).build();
    }

    @Before
    public void initTest() {
        timeInterval = new TimeInterval();
        timeInterval.setName(DEFAULT_NAME);
        timeInterval.setCustom(DEFAULT_CUSTOM);
        timeInterval.setIntervalMillis(DEFAULT_INTERVAL_MILLIS);
        timeInterval.setInterval(DEFAULT_INTERVAL);
    }

    @Test
    @Transactional
    public void createTimeInterval() throws Exception {
        // Validate the database is empty
        assertThat(timeIntervalRepository.findAll()).hasSize(0);

        // Create the TimeInterval
        restTimeIntervalMockMvc.perform(post("/app/rest/timeInterval")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(timeInterval)))
                .andExpect(status().isOk());

        // Validate the TimeInterval in the database
        List<TimeInterval> timeIntervals = timeIntervalRepository.findAll();
        assertThat(timeIntervals).hasSize(1);
        TimeInterval testTimeInterval = timeIntervals.iterator().next();
        assertThat(testTimeInterval.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTimeInterval.getCustom()).isEqualTo(DEFAULT_CUSTOM);
        assertThat(testTimeInterval.getIntervalMillis()).isEqualTo(DEFAULT_INTERVAL_MILLIS);
        assertThat(testTimeInterval.getInterval()).isEqualTo(DEFAULT_INTERVAL);
    }

    @Test
    @Transactional
    public void getAllTimeIntervals() throws Exception {
        // Initialize the database
        timeIntervalRepository.saveAndFlush(timeInterval);

        // Get all the timeIntervals
        restTimeIntervalMockMvc.perform(get("/app/rest/timeInterval"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].name").value(DEFAULT_NAME.toString()))
                .andExpect(jsonPath("$.[0].custom").value(DEFAULT_CUSTOM.booleanValue()))
                .andExpect(jsonPath("$.[0].intervalMillis").value(DEFAULT_INTERVAL_MILLIS.intValue()))
                .andExpect(jsonPath("$.[0].interval").value(DEFAULT_INTERVAL.intValue()));
    }

    @Test
    @Transactional
    public void getTimeInterval() throws Exception {
        // Initialize the database
        timeIntervalRepository.saveAndFlush(timeInterval);

        // Get the timeInterval
        restTimeIntervalMockMvc.perform(get("/app/rest/timeInterval/{id}", timeInterval.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(timeInterval.getId().intValue()))
                .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
                .andExpect(jsonPath("$.custom").value(DEFAULT_CUSTOM.booleanValue()))
                .andExpect(jsonPath("$.intervalMillis").value(DEFAULT_INTERVAL_MILLIS.intValue()))
                .andExpect(jsonPath("$.interval").value(DEFAULT_INTERVAL.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingTimeInterval() throws Exception {
        // Get the timeInterval
        restTimeIntervalMockMvc.perform(get("/app/rest/timeInterval/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTimeInterval() throws Exception {
        // Initialize the database
        timeIntervalRepository.saveAndFlush(timeInterval);

        // Update the timeInterval
        timeInterval.setName(UPDATED_NAME);
        timeInterval.setCustom(UPDATED_CUSTOM);
        timeInterval.setIntervalMillis(UPDATED_INTERVAL_MILLIS);
        timeInterval.setInterval(UPDATED_INTERVAL);
        restTimeIntervalMockMvc.perform(post("/app/rest/timeInterval")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(timeInterval)))
                .andExpect(status().isOk());

        // Validate the TimeInterval in the database
        List<TimeInterval> timeIntervals = timeIntervalRepository.findAll();
        assertThat(timeIntervals).hasSize(1);
        TimeInterval testTimeInterval = timeIntervals.iterator().next();
        assertThat(testTimeInterval.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTimeInterval.getCustom()).isEqualTo(UPDATED_CUSTOM);
        assertThat(testTimeInterval.getIntervalMillis()).isEqualTo(UPDATED_INTERVAL_MILLIS);
        assertThat(testTimeInterval.getInterval()).isEqualTo(UPDATED_INTERVAL);
    }

    @Test
    @Transactional
    public void deleteTimeInterval() throws Exception {
        // Initialize the database
        timeIntervalRepository.saveAndFlush(timeInterval);

        // Get the timeInterval
        restTimeIntervalMockMvc.perform(delete("/app/rest/timeInterval/{id}", timeInterval.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<TimeInterval> timeIntervals = timeIntervalRepository.findAll();
        assertThat(timeIntervals).hasSize(0);
    }
}
