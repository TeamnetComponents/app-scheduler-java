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
import ro.teamnet.scheduler.domain.RecurrentHour;
import ro.teamnet.scheduler.repository.RecurrentHourRepository;
import ro.teamnet.scheduler.service.RecurrentHourService;
import ro.teamnet.web.rest.TestUtil;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the RecurrentHourResource REST controller.
 *
 * @see RecurrentHourResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SchedulerTestApplication.class)
@WebAppConfiguration
@IntegrationTest
@ActiveProfiles("test-scheduler")
public class RecurrentHourResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");


    private static final Integer DEFAULT_HOUR = 0;
    private static final Integer UPDATED_HOUR = 1;

    @Inject
    private RecurrentHourRepository recurrentHourRepository;

    @Inject
    RecurrentHourService service;

    private MockMvc restRecurrentHourMockMvc;

    private RecurrentHour recurrentHour;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RecurrentHourResource recurrentHourResource = new RecurrentHourResource(service);
        this.restRecurrentHourMockMvc = MockMvcBuilders.standaloneSetup(recurrentHourResource).build();
    }

    @Before
    public void initTest() {
        recurrentHour = new RecurrentHour();
        recurrentHour.setHour(DEFAULT_HOUR);
    }

    @Test
    @Transactional
    public void createRecurrentHour() throws Exception {
        // Validate the database is empty
        assertThat(recurrentHourRepository.findAll()).hasSize(0);

        // Create the RecurrentHour
        restRecurrentHourMockMvc.perform(post("/app/rest/recurrentHour")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(recurrentHour)))
                .andExpect(status().isOk());

        // Validate the RecurrentHour in the database
        List<RecurrentHour> recurrentHours = recurrentHourRepository.findAll();
        assertThat(recurrentHours).hasSize(1);
        RecurrentHour testRecurrentHour = recurrentHours.iterator().next();
        assertThat(testRecurrentHour.getHour()).isEqualTo(DEFAULT_HOUR);
    }

    @Test
    @Transactional
    public void getAllRecurrentHours() throws Exception {
        // Initialize the database
        recurrentHourRepository.saveAndFlush(recurrentHour);

        // Get all the recurrentHours
        restRecurrentHourMockMvc.perform(get("/app/rest/recurrentHour"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].hour").value(DEFAULT_HOUR));
    }

    @Test
    @Transactional
    public void getRecurrentHour() throws Exception {
        // Initialize the database
        recurrentHourRepository.saveAndFlush(recurrentHour);

        // Get the recurrentHour
        restRecurrentHourMockMvc.perform(get("/app/rest/recurrentHour/{id}", recurrentHour.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(recurrentHour.getId().intValue()))
            .andExpect(jsonPath("$.hour").value(DEFAULT_HOUR));
    }

    @Test
    @Transactional
    public void getNonExistingRecurrentHour() throws Exception {
        // Get the recurrentHour
        restRecurrentHourMockMvc.perform(get("/app/rest/recurrentHour/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRecurrentHour() throws Exception {
        // Initialize the database
        recurrentHourRepository.saveAndFlush(recurrentHour);

        // Update the recurrentHour
        recurrentHour.setHour(UPDATED_HOUR);
        restRecurrentHourMockMvc.perform(post("/app/rest/recurrentHour")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(recurrentHour)))
                .andExpect(status().isOk());

        // Validate the RecurrentHour in the database
        List<RecurrentHour> recurrentHours = recurrentHourRepository.findAll();
        assertThat(recurrentHours).hasSize(1);
        RecurrentHour testRecurrentHour = recurrentHours.iterator().next();
        assertThat(testRecurrentHour.getHour()).isEqualTo(UPDATED_HOUR);
    }

    @Test
    @Transactional
    public void deleteRecurrentHour() throws Exception {
        // Initialize the database
        recurrentHourRepository.saveAndFlush(recurrentHour);

        // Get the recurrentHour
        restRecurrentHourMockMvc.perform(delete("/app/rest/recurrentHour/{id}", recurrentHour.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<RecurrentHour> recurrentHours = recurrentHourRepository.findAll();
        assertThat(recurrentHours).hasSize(0);
    }
}
