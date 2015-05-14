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
import ro.teamnet.scheduler.domain.DayOfWeek;
import ro.teamnet.scheduler.repository.DayOfWeekRepository;
import ro.teamnet.scheduler.service.DayOfWeekService;
import ro.teamnet.web.rest.TestUtil;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the DayOfWeekResource REST controller.
 *
 * @see DayOfWeekResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SchedulerTestApplication.class)
@WebAppConfiguration
@IntegrationTest
@ActiveProfiles("test-scheduler")
public class DayOfWeekResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");

    private static final String DEFAULT_CODE = "SAMPLE_TEXT";
    private static final String UPDATED_CODE = "UPDATED_TEXT";
    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";

    private static final Integer DEFAULT_VALUE = 0;
    private static final Integer UPDATED_VALUE = 1;

    @Inject
    private DayOfWeekRepository dayOfWeekRepository;

    @Inject
    DayOfWeekService service;

    private MockMvc restDayOfWeekMockMvc;

    private DayOfWeek dayOfWeek;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DayOfWeekResource dayOfWeekResource = new DayOfWeekResource(service);
        this.restDayOfWeekMockMvc = MockMvcBuilders.standaloneSetup(dayOfWeekResource).build();
    }

    @Before
    public void initTest() {
        dayOfWeek = new DayOfWeek();
        dayOfWeek.setCode(DEFAULT_CODE);
        dayOfWeek.setName(DEFAULT_NAME);
        dayOfWeek.setValue(DEFAULT_VALUE);
    }

    @Test
    @Transactional
    public void createDayOfWeek() throws Exception {
        // Validate the database is empty
        assertThat(dayOfWeekRepository.findAll()).hasSize(0);

        // Create the DayOfWeek
        restDayOfWeekMockMvc.perform(post("/app/rest/dayOfWeek")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dayOfWeek)))
                .andExpect(status().isOk());

        // Validate the DayOfWeek in the database
        List<DayOfWeek> dayOfWeeks = dayOfWeekRepository.findAll();
        assertThat(dayOfWeeks).hasSize(1);
        DayOfWeek testDayOfWeek = dayOfWeeks.iterator().next();
        assertThat(testDayOfWeek.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testDayOfWeek.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDayOfWeek.getValue()).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    @Transactional
    public void getAllDayOfWeeks() throws Exception {
        // Initialize the database
        dayOfWeekRepository.saveAndFlush(dayOfWeek);

        // Get all the dayOfWeeks
        restDayOfWeekMockMvc.perform(get("/app/rest/dayOfWeek"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].code").value(DEFAULT_CODE.toString()))
                .andExpect(jsonPath("$.[0].name").value(DEFAULT_NAME.toString()))
                .andExpect(jsonPath("$.[0].value").value(DEFAULT_VALUE));
    }

    @Test
    @Transactional
    public void getDayOfWeek() throws Exception {
        // Initialize the database
        dayOfWeekRepository.saveAndFlush(dayOfWeek);

        // Get the dayOfWeek
        restDayOfWeekMockMvc.perform(get("/app/rest/dayOfWeek/{id}", dayOfWeek.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(dayOfWeek.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE));
    }

    @Test
    @Transactional
    public void getNonExistingDayOfWeek() throws Exception {
        // Get the dayOfWeek
        restDayOfWeekMockMvc.perform(get("/app/rest/dayOfWeek/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDayOfWeek() throws Exception {
        // Initialize the database
        dayOfWeekRepository.saveAndFlush(dayOfWeek);

        // Update the dayOfWeek
        dayOfWeek.setCode(UPDATED_CODE);
        dayOfWeek.setName(UPDATED_NAME);
        dayOfWeek.setValue(UPDATED_VALUE);
        restDayOfWeekMockMvc.perform(post("/app/rest/dayOfWeek")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dayOfWeek)))
                .andExpect(status().isOk());

        // Validate the DayOfWeek in the database
        List<DayOfWeek> dayOfWeeks = dayOfWeekRepository.findAll();
        assertThat(dayOfWeeks).hasSize(1);
        DayOfWeek testDayOfWeek = dayOfWeeks.iterator().next();
        assertThat(testDayOfWeek.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testDayOfWeek.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDayOfWeek.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    public void deleteDayOfWeek() throws Exception {
        // Initialize the database
        dayOfWeekRepository.saveAndFlush(dayOfWeek);

        // Get the dayOfWeek
        restDayOfWeekMockMvc.perform(delete("/app/rest/dayOfWeek/{id}", dayOfWeek.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<DayOfWeek> dayOfWeeks = dayOfWeekRepository.findAll();
        assertThat(dayOfWeeks).hasSize(0);
    }
}
