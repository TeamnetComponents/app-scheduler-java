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
import ro.teamnet.scheduler.domain.RecurrentDay;
import ro.teamnet.scheduler.repository.RecurrentDayRepository;
import ro.teamnet.scheduler.service.RecurrentDayService;
import ro.teamnet.web.rest.TestUtil;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the RecurrentDayResource REST controller.
 *
 * @see RecurrentDayResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SchedulerTestApplication.class)
@WebAppConfiguration
@IntegrationTest
@ActiveProfiles("test-scheduler")
public class RecurrentDayResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");


    private static final Integer DEFAULT_DAY = 0;
    private static final Integer UPDATED_DAY = 1;

    @Inject
    private RecurrentDayRepository recurrentDayRepository;

    @Inject
    RecurrentDayService service;

    private MockMvc restRecurrentDayMockMvc;

    private RecurrentDay recurrentDay;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RecurrentDayResource recurrentDayResource = new RecurrentDayResource(service);
        this.restRecurrentDayMockMvc = MockMvcBuilders.standaloneSetup(recurrentDayResource).build();
    }

    @Before
    public void initTest() {
        recurrentDay = new RecurrentDay();
        recurrentDay.setDay(DEFAULT_DAY);
    }

    @Test
    @Transactional
    public void createRecurrentDay() throws Exception {
        // Validate the database is empty
        assertThat(recurrentDayRepository.findAll()).hasSize(0);

        // Create the RecurrentDay
        restRecurrentDayMockMvc.perform(post("/app/rest/recurrentDay")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(recurrentDay)))
                .andExpect(status().isOk());

        // Validate the RecurrentDay in the database
        List<RecurrentDay> recurrentDays = recurrentDayRepository.findAll();
        assertThat(recurrentDays).hasSize(1);
        RecurrentDay testRecurrentDay = recurrentDays.iterator().next();
        assertThat(testRecurrentDay.getDay()).isEqualTo(DEFAULT_DAY);
    }

    @Test
    @Transactional
    public void getAllRecurrentDays() throws Exception {
        // Initialize the database
        recurrentDayRepository.saveAndFlush(recurrentDay);

        // Get all the recurrentDays
        restRecurrentDayMockMvc.perform(get("/app/rest/recurrentDay"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].day").value(DEFAULT_DAY));
    }

    @Test
    @Transactional
    public void getRecurrentDay() throws Exception {
        // Initialize the database
        recurrentDayRepository.saveAndFlush(recurrentDay);

        // Get the recurrentDay
        restRecurrentDayMockMvc.perform(get("/app/rest/recurrentDay/{id}", recurrentDay.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(recurrentDay.getId().intValue()))
            .andExpect(jsonPath("$.day").value(DEFAULT_DAY));
    }

    @Test
    @Transactional
    public void getNonExistingRecurrentDay() throws Exception {
        // Get the recurrentDay
        restRecurrentDayMockMvc.perform(get("/app/rest/recurrentDay/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRecurrentDay() throws Exception {
        // Initialize the database
        recurrentDayRepository.saveAndFlush(recurrentDay);

        // Update the recurrentDay
        recurrentDay.setDay(UPDATED_DAY);
        restRecurrentDayMockMvc.perform(post("/app/rest/recurrentDay")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(recurrentDay)))
                .andExpect(status().isOk());

        // Validate the RecurrentDay in the database
        List<RecurrentDay> recurrentDays = recurrentDayRepository.findAll();
        assertThat(recurrentDays).hasSize(1);
        RecurrentDay testRecurrentDay = recurrentDays.iterator().next();
        assertThat(testRecurrentDay.getDay()).isEqualTo(UPDATED_DAY);
    }

    @Test
    @Transactional
    public void deleteRecurrentDay() throws Exception {
        // Initialize the database
        recurrentDayRepository.saveAndFlush(recurrentDay);

        // Get the recurrentDay
        restRecurrentDayMockMvc.perform(delete("/app/rest/recurrentDay/{id}", recurrentDay.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<RecurrentDay> recurrentDays = recurrentDayRepository.findAll();
        assertThat(recurrentDays).hasSize(0);
    }
}
