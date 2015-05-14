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
import ro.teamnet.scheduler.domain.TimeUnit;
import ro.teamnet.scheduler.repository.TimeUnitRepository;
import ro.teamnet.scheduler.service.TimeUnitService;
import ro.teamnet.web.rest.TestUtil;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the TimeUnitResource REST controller.
 *
 * @see TimeUnitResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SchedulerTestApplication.class)
@WebAppConfiguration
@IntegrationTest
@ActiveProfiles("test-scheduler")
public class TimeUnitResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");

    private static final String DEFAULT_CODE = "SAMPLE_TEXT";
    private static final String UPDATED_CODE = "UPDATED_TEXT";
    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";
    private static final String UPDATED_DESCRIPTION = "UPDATED_TEXT";

    private static final Long DEFAULT_MILLIS = 0L;
    private static final Long UPDATED_MILLIS = 1L;

    @Inject
    private TimeUnitRepository timeUnitRepository;

    @Inject
    TimeUnitService service;

    private MockMvc restTimeUnitMockMvc;

    private TimeUnit timeUnit;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TimeUnitResource timeUnitResource = new TimeUnitResource(service);
        this.restTimeUnitMockMvc = MockMvcBuilders.standaloneSetup(timeUnitResource).build();
    }

    @Before
    public void initTest() {
        timeUnit = new TimeUnit();
        timeUnit.setCode(DEFAULT_CODE);
        timeUnit.setName(DEFAULT_NAME);
        timeUnit.setDescription(DEFAULT_DESCRIPTION);
        timeUnit.setMillis(DEFAULT_MILLIS);
    }

    @Test
    @Transactional
    public void createTimeUnit() throws Exception {
        // Validate the database is empty
        assertThat(timeUnitRepository.findAll()).hasSize(0);

        // Create the TimeUnit
        restTimeUnitMockMvc.perform(post("/app/rest/timeUnit")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(timeUnit)))
                .andExpect(status().isOk());

        // Validate the TimeUnit in the database
        List<TimeUnit> timeUnits = timeUnitRepository.findAll();
        assertThat(timeUnits).hasSize(1);
        TimeUnit testTimeUnit = timeUnits.iterator().next();
        assertThat(testTimeUnit.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testTimeUnit.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTimeUnit.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTimeUnit.getMillis()).isEqualTo(DEFAULT_MILLIS);
    }

    @Test
    @Transactional
    public void getAllTimeUnits() throws Exception {
        // Initialize the database
        timeUnitRepository.saveAndFlush(timeUnit);

        // Get all the timeUnits
        restTimeUnitMockMvc.perform(get("/app/rest/timeUnit"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].code").value(DEFAULT_CODE.toString()))
                .andExpect(jsonPath("$.[0].name").value(DEFAULT_NAME.toString()))
                .andExpect(jsonPath("$.[0].description").value(DEFAULT_DESCRIPTION.toString()))
                .andExpect(jsonPath("$.[0].millis").value(DEFAULT_MILLIS.intValue()));
    }

    @Test
    @Transactional
    public void getTimeUnit() throws Exception {
        // Initialize the database
        timeUnitRepository.saveAndFlush(timeUnit);

        // Get the timeUnit
        restTimeUnitMockMvc.perform(get("/app/rest/timeUnit/{id}", timeUnit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(timeUnit.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.millis").value(DEFAULT_MILLIS.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingTimeUnit() throws Exception {
        // Get the timeUnit
        restTimeUnitMockMvc.perform(get("/app/rest/timeUnit/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTimeUnit() throws Exception {
        // Initialize the database
        timeUnitRepository.saveAndFlush(timeUnit);

        // Update the timeUnit
        timeUnit.setCode(UPDATED_CODE);
        timeUnit.setName(UPDATED_NAME);
        timeUnit.setDescription(UPDATED_DESCRIPTION);
        timeUnit.setMillis(UPDATED_MILLIS);
        restTimeUnitMockMvc.perform(post("/app/rest/timeUnit")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(timeUnit)))
                .andExpect(status().isOk());

        // Validate the TimeUnit in the database
        List<TimeUnit> timeUnits = timeUnitRepository.findAll();
        assertThat(timeUnits).hasSize(1);
        TimeUnit testTimeUnit = timeUnits.iterator().next();
        assertThat(testTimeUnit.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testTimeUnit.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTimeUnit.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTimeUnit.getMillis()).isEqualTo(UPDATED_MILLIS);
    }

    @Test
    @Transactional
    public void deleteTimeUnit() throws Exception {
        // Initialize the database
        timeUnitRepository.saveAndFlush(timeUnit);

        // Get the timeUnit
        restTimeUnitMockMvc.perform(delete("/app/rest/timeUnit/{id}", timeUnit.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<TimeUnit> timeUnits = timeUnitRepository.findAll();
        assertThat(timeUnits).hasSize(0);
    }
}
