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
import ro.teamnet.scheduler.domain.RecurrentMinute;
import ro.teamnet.scheduler.repository.RecurrentMinuteRepository;
import ro.teamnet.scheduler.service.RecurrentMinuteService;
import ro.teamnet.web.rest.TestUtil;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the RecurrentMinuteResource REST controller.
 *
 * @see RecurrentMinuteResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SchedulerTestApplication.class)
@WebAppConfiguration
@IntegrationTest
@ActiveProfiles("test-scheduler")
public class RecurrentMinuteResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");


    private static final Integer DEFAULT_MINUTE = 0;
    private static final Integer UPDATED_MINUTE = 1;

    @Inject
    private RecurrentMinuteRepository recurrentMinuteRepository;

    @Inject
    RecurrentMinuteService service;

    private MockMvc restRecurrentMinuteMockMvc;

    private RecurrentMinute recurrentMinute;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RecurrentMinuteResource recurrentMinuteResource = new RecurrentMinuteResource(service);
        this.restRecurrentMinuteMockMvc = MockMvcBuilders.standaloneSetup(recurrentMinuteResource).build();
    }

    @Before
    public void initTest() {
        recurrentMinute = new RecurrentMinute();
        recurrentMinute.setMinute(DEFAULT_MINUTE);
    }

    @Test
    @Transactional
    public void createRecurrentMinute() throws Exception {
        // Validate the database is empty
        assertThat(recurrentMinuteRepository.findAll()).hasSize(0);

        // Create the RecurrentMinute
        restRecurrentMinuteMockMvc.perform(post("/app/rest/recurrentMinute")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(recurrentMinute)))
                .andExpect(status().isOk());

        // Validate the RecurrentMinute in the database
        List<RecurrentMinute> recurrentMinutes = recurrentMinuteRepository.findAll();
        assertThat(recurrentMinutes).hasSize(1);
        RecurrentMinute testRecurrentMinute = recurrentMinutes.iterator().next();
        assertThat(testRecurrentMinute.getMinute()).isEqualTo(DEFAULT_MINUTE);
    }

    @Test
    @Transactional
    public void getAllRecurrentMinutes() throws Exception {
        // Initialize the database
        recurrentMinuteRepository.saveAndFlush(recurrentMinute);

        // Get all the recurrentMinutes
        restRecurrentMinuteMockMvc.perform(get("/app/rest/recurrentMinute"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].minute").value(DEFAULT_MINUTE));
    }

    @Test
    @Transactional
    public void getRecurrentMinute() throws Exception {
        // Initialize the database
        recurrentMinuteRepository.saveAndFlush(recurrentMinute);

        // Get the recurrentMinute
        restRecurrentMinuteMockMvc.perform(get("/app/rest/recurrentMinute/{id}", recurrentMinute.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(recurrentMinute.getId().intValue()))
            .andExpect(jsonPath("$.minute").value(DEFAULT_MINUTE));
    }

    @Test
    @Transactional
    public void getNonExistingRecurrentMinute() throws Exception {
        // Get the recurrentMinute
        restRecurrentMinuteMockMvc.perform(get("/app/rest/recurrentMinute/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRecurrentMinute() throws Exception {
        // Initialize the database
        recurrentMinuteRepository.saveAndFlush(recurrentMinute);

        // Update the recurrentMinute
        recurrentMinute.setMinute(UPDATED_MINUTE);
        restRecurrentMinuteMockMvc.perform(post("/app/rest/recurrentMinute")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(recurrentMinute)))
                .andExpect(status().isOk());

        // Validate the RecurrentMinute in the database
        List<RecurrentMinute> recurrentMinutes = recurrentMinuteRepository.findAll();
        assertThat(recurrentMinutes).hasSize(1);
        RecurrentMinute testRecurrentMinute = recurrentMinutes.iterator().next();
        assertThat(testRecurrentMinute.getMinute()).isEqualTo(UPDATED_MINUTE);
    }

    @Test
    @Transactional
    public void deleteRecurrentMinute() throws Exception {
        // Initialize the database
        recurrentMinuteRepository.saveAndFlush(recurrentMinute);

        // Get the recurrentMinute
        restRecurrentMinuteMockMvc.perform(delete("/app/rest/recurrentMinute/{id}", recurrentMinute.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<RecurrentMinute> recurrentMinutes = recurrentMinuteRepository.findAll();
        assertThat(recurrentMinutes).hasSize(0);
    }
}
