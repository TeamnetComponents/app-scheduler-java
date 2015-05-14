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
import ro.teamnet.scheduler.domain.RecurrentYear;
import ro.teamnet.scheduler.repository.RecurrentYearRepository;
import ro.teamnet.scheduler.service.RecurrentYearService;
import ro.teamnet.web.rest.TestUtil;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the RecurrentYearResource REST controller.
 *
 * @see RecurrentYearResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SchedulerTestApplication.class)
@WebAppConfiguration
@IntegrationTest
@ActiveProfiles("test-scheduler")
public class RecurrentYearResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");


    private static final Integer DEFAULT_YEAR = 0;
    private static final Integer UPDATED_YEAR = 1;

    @Inject
    private RecurrentYearRepository recurrentYearRepository;

    @Inject
    RecurrentYearService service;

    private MockMvc restRecurrentYearMockMvc;

    private RecurrentYear recurrentYear;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RecurrentYearResource recurrentYearResource = new RecurrentYearResource(service);
        this.restRecurrentYearMockMvc = MockMvcBuilders.standaloneSetup(recurrentYearResource).build();
    }

    @Before
    public void initTest() {
        recurrentYear = new RecurrentYear();
        recurrentYear.setYear(DEFAULT_YEAR);
    }

    @Test
    @Transactional
    public void createRecurrentYear() throws Exception {
        // Validate the database is empty
        assertThat(recurrentYearRepository.findAll()).hasSize(0);

        // Create the RecurrentYear
        restRecurrentYearMockMvc.perform(post("/app/rest/recurrentYear")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(recurrentYear)))
                .andExpect(status().isOk());

        // Validate the RecurrentYear in the database
        List<RecurrentYear> recurrentYears = recurrentYearRepository.findAll();
        assertThat(recurrentYears).hasSize(1);
        RecurrentYear testRecurrentYear = recurrentYears.iterator().next();
        assertThat(testRecurrentYear.getYear()).isEqualTo(DEFAULT_YEAR);
    }

    @Test
    @Transactional
    public void getAllRecurrentYears() throws Exception {
        // Initialize the database
        recurrentYearRepository.saveAndFlush(recurrentYear);

        // Get all the recurrentYears
        restRecurrentYearMockMvc.perform(get("/app/rest/recurrentYear"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].year").value(DEFAULT_YEAR));
    }

    @Test
    @Transactional
    public void getRecurrentYear() throws Exception {
        // Initialize the database
        recurrentYearRepository.saveAndFlush(recurrentYear);

        // Get the recurrentYear
        restRecurrentYearMockMvc.perform(get("/app/rest/recurrentYear/{id}", recurrentYear.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(recurrentYear.getId().intValue()))
            .andExpect(jsonPath("$.year").value(DEFAULT_YEAR));
    }

    @Test
    @Transactional
    public void getNonExistingRecurrentYear() throws Exception {
        // Get the recurrentYear
        restRecurrentYearMockMvc.perform(get("/app/rest/recurrentYear/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRecurrentYear() throws Exception {
        // Initialize the database
        recurrentYearRepository.saveAndFlush(recurrentYear);

        // Update the recurrentYear
        recurrentYear.setYear(UPDATED_YEAR);
        restRecurrentYearMockMvc.perform(post("/app/rest/recurrentYear")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(recurrentYear)))
                .andExpect(status().isOk());

        // Validate the RecurrentYear in the database
        List<RecurrentYear> recurrentYears = recurrentYearRepository.findAll();
        assertThat(recurrentYears).hasSize(1);
        RecurrentYear testRecurrentYear = recurrentYears.iterator().next();
        assertThat(testRecurrentYear.getYear()).isEqualTo(UPDATED_YEAR);
    }

    @Test
    @Transactional
    public void deleteRecurrentYear() throws Exception {
        // Initialize the database
        recurrentYearRepository.saveAndFlush(recurrentYear);

        // Get the recurrentYear
        restRecurrentYearMockMvc.perform(delete("/app/rest/recurrentYear/{id}", recurrentYear.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<RecurrentYear> recurrentYears = recurrentYearRepository.findAll();
        assertThat(recurrentYears).hasSize(0);
    }
}
