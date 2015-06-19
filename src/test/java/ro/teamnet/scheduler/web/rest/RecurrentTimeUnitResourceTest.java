package ro.teamnet.scheduler.web.rest;

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
import ro.teamnet.scheduler.domain.RecurrentTimeUnit;
import ro.teamnet.scheduler.repository.RecurrentTimeUnitRepository;
import ro.teamnet.scheduler.service.RecurrentTimeUnitService;
import ro.teamnet.web.rest.TestUtil;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the RecurrentTimeUnitResource REST controller.
 *
 * @see RecurrentTimeUnitResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SchedulerTestApplication.class)
@WebAppConfiguration
@IntegrationTest
@ActiveProfiles("test-scheduler")
public class RecurrentTimeUnitResourceTest {

    private static final Integer DEFAULT_VALUE = 0;
    private static final Integer UPDATED_VALUE = 1;

    @Inject
    private RecurrentTimeUnitRepository recurrentTimeUnitRepository;

    @Inject
    RecurrentTimeUnitService service;

    private MockMvc restRecurrentTimeUnitMockMvc;

    private RecurrentTimeUnit recurrentTimeUnit;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RecurrentTimeUnitResource recurrentTimeUnitResource = new RecurrentTimeUnitResource(service);
        this.restRecurrentTimeUnitMockMvc = MockMvcBuilders.standaloneSetup(recurrentTimeUnitResource).build();
    }

    @Before
    public void initTest() {
        recurrentTimeUnit = new RecurrentTimeUnit();
        recurrentTimeUnit.setValue(DEFAULT_VALUE);
    }

    @Test
    @Transactional
    public void createRecurrentTimeUnit() throws Exception {
        // Validate the database is empty
        assertThat(recurrentTimeUnitRepository.findAll()).hasSize(0);

        // Create the RecurrentTimeUnit
        restRecurrentTimeUnitMockMvc.perform(post("/app/rest/recurrentTimeUnit")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(recurrentTimeUnit)))
                .andExpect(status().isOk());

        // Validate the RecurrentTimeUnit in the database
        List<RecurrentTimeUnit> recurrentTimeUnits = recurrentTimeUnitRepository.findAll();
        assertThat(recurrentTimeUnits).hasSize(1);
        RecurrentTimeUnit testRecurrentTimeUnit = recurrentTimeUnits.iterator().next();
        assertThat(testRecurrentTimeUnit.getValue()).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    @Transactional
    public void getAllRecurrentTimeUnits() throws Exception {
        // Initialize the database
        recurrentTimeUnitRepository.saveAndFlush(recurrentTimeUnit);

        // Get all the recurrentTimeUnits
        restRecurrentTimeUnitMockMvc.perform(get("/app/rest/recurrentTimeUnit"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].value").value(DEFAULT_VALUE));
    }

    @Test
    @Transactional
    public void getRecurrentTimeUnit() throws Exception {
        // Initialize the database
        recurrentTimeUnitRepository.saveAndFlush(recurrentTimeUnit);

        // Get the recurrentTimeUnit
        restRecurrentTimeUnitMockMvc.perform(get("/app/rest/recurrentTimeUnit/{id}", recurrentTimeUnit.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(recurrentTimeUnit.getId().intValue()))
                .andExpect(jsonPath("$.value").value(DEFAULT_VALUE));
    }

    @Test
    @Transactional
    public void getNonExistingRecurrentTimeUnit() throws Exception {
        // Get the recurrentTimeUnit
        restRecurrentTimeUnitMockMvc.perform(get("/app/rest/recurrentTimeUnit/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRecurrentTimeUnit() throws Exception {
        // Initialize the database
        recurrentTimeUnitRepository.saveAndFlush(recurrentTimeUnit);

        // Update the recurrentTimeUnit
        recurrentTimeUnit.setValue(UPDATED_VALUE);
        restRecurrentTimeUnitMockMvc.perform(post("/app/rest/recurrentTimeUnit")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(recurrentTimeUnit)))
                .andExpect(status().isOk());

        // Validate the RecurrentTimeUnit in the database
        List<RecurrentTimeUnit> recurrentTimeUnits = recurrentTimeUnitRepository.findAll();
        assertThat(recurrentTimeUnits).hasSize(1);
        RecurrentTimeUnit testRecurrentTimeUnit = recurrentTimeUnits.iterator().next();
        assertThat(testRecurrentTimeUnit.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    public void deleteRecurrentTimeUnit() throws Exception {
        // Initialize the database
        recurrentTimeUnitRepository.saveAndFlush(recurrentTimeUnit);

        // Get the recurrentTimeUnit
        restRecurrentTimeUnitMockMvc.perform(delete("/app/rest/recurrentTimeUnit/{id}", recurrentTimeUnit.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<RecurrentTimeUnit> recurrentTimeUnits = recurrentTimeUnitRepository.findAll();
        assertThat(recurrentTimeUnits).hasSize(0);
    }
}
