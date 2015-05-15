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
import ro.teamnet.scheduler.domain.Month;
import ro.teamnet.scheduler.repository.MonthRepository;
import ro.teamnet.scheduler.service.MonthService;
import ro.teamnet.web.rest.TestUtil;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the MonthResource REST controller.
 *
 * @see MonthResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SchedulerTestApplication.class)
@WebAppConfiguration
@IntegrationTest
@ActiveProfiles("test-scheduler")
public class MonthResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");

    private static final String DEFAULT_CODE = "SAMPLE_TEXT";
    private static final String UPDATED_CODE = "UPDATED_TEXT";
    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";

    private static final Integer DEFAULT_VALUE = 0;
    private static final Integer UPDATED_VALUE = 1;

    @Inject
    private MonthRepository monthRepository;

    @Inject
    MonthService service;

    private MockMvc restMonthMockMvc;

    private Month month;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MonthResource monthResource = new MonthResource(service);
        this.restMonthMockMvc = MockMvcBuilders.standaloneSetup(monthResource).build();
    }

    @Before
    public void initTest() {
        month = new Month();
        month.setCode(DEFAULT_CODE);
        month.setName(DEFAULT_NAME);
        month.setValue(DEFAULT_VALUE);
    }

    @Test
    @Transactional
    public void createMonth() throws Exception {
        // Validate the database is empty
        assertThat(monthRepository.findAll()).hasSize(0);

        // Create the Month
        restMonthMockMvc.perform(post("/app/rest/month")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(month)))
                .andExpect(status().isOk());

        // Validate the Month in the database
        List<Month> months = monthRepository.findAll();
        assertThat(months).hasSize(1);
        Month testMonth = months.iterator().next();
        assertThat(testMonth.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testMonth.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMonth.getValue()).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    @Transactional
    public void getAllMonths() throws Exception {
        // Initialize the database
        monthRepository.saveAndFlush(month);

        // Get all the months
        restMonthMockMvc.perform(get("/app/rest/month"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].code").value(DEFAULT_CODE.toString()))
                .andExpect(jsonPath("$.[0].name").value(DEFAULT_NAME.toString()))
                .andExpect(jsonPath("$.[0].value").value(DEFAULT_VALUE));
    }

    @Test
    @Transactional
    public void getMonth() throws Exception {
        // Initialize the database
        monthRepository.saveAndFlush(month);

        // Get the month
        restMonthMockMvc.perform(get("/app/rest/month/{id}", month.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(month.getId().intValue()))
                .andExpect(jsonPath("$.code").value(DEFAULT_CODE.toString()))
                .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
                .andExpect(jsonPath("$.value").value(DEFAULT_VALUE));
    }

    @Test
    @Transactional
    public void getNonExistingMonth() throws Exception {
        // Get the month
        restMonthMockMvc.perform(get("/app/rest/month/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMonth() throws Exception {
        // Initialize the database
        monthRepository.saveAndFlush(month);

        // Update the month
        month.setCode(UPDATED_CODE);
        month.setName(UPDATED_NAME);
        month.setValue(UPDATED_VALUE);
        restMonthMockMvc.perform(post("/app/rest/month")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(month)))
                .andExpect(status().isOk());

        // Validate the Month in the database
        List<Month> months = monthRepository.findAll();
        assertThat(months).hasSize(1);
        Month testMonth = months.iterator().next();
        assertThat(testMonth.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testMonth.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMonth.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    public void deleteMonth() throws Exception {
        // Initialize the database
        monthRepository.saveAndFlush(month);

        // Get the month
        restMonthMockMvc.perform(delete("/app/rest/month/{id}", month.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Month> months = monthRepository.findAll();
        assertThat(months).hasSize(0);
    }
}
