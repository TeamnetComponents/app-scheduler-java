
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
import ro.teamnet.scheduler.domain.ExecutionData;
import ro.teamnet.scheduler.repository.ExecutionDataRepository;
import ro.teamnet.scheduler.service.ExecutionDataService;
import ro.teamnet.web.rest.TestUtil;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ExecutionDataResource REST controller.
 *
 * @see ExecutionDataResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SchedulerTestApplication.class)
@WebAppConfiguration
@IntegrationTest
@ActiveProfiles("test-scheduler")
public class ExecutionDataResourceTest {
    private static final Long DEFAULT_DATA_ID = 0L;
    private static final Long UPDATED_DATA_ID = 1L;

    @Inject
    private ExecutionDataRepository executionDataRepository;

    @Inject
    ExecutionDataService service;

    private MockMvc restExecutionDataMockMvc;

    private ExecutionData executionData;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ExecutionDataResource executionDataResource = new ExecutionDataResource(service);
        this.restExecutionDataMockMvc = MockMvcBuilders.standaloneSetup(executionDataResource).build();
    }

    @Before
    public void initTest() {
        executionData = new ExecutionData();
        executionData.setDataId(DEFAULT_DATA_ID);
    }

    @Test
    @Transactional
    public void createExecutionData() throws Exception {
        // Validate the database is empty
        assertThat(executionDataRepository.findAll()).hasSize(0);

        // Create the ExecutionData
        restExecutionDataMockMvc.perform(post("/app/rest/executionData")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(executionData)))
                .andExpect(status().isOk());

        // Validate the ExecutionData in the database
        List<ExecutionData> executionDatas = executionDataRepository.findAll();
        assertThat(executionDatas).hasSize(1);
        ExecutionData testExecutionData = executionDatas.iterator().next();
        assertThat(testExecutionData.getDataId()).isEqualTo(DEFAULT_DATA_ID);
    }

    @Test
    @Transactional
    public void getAllExecutionDatas() throws Exception {
        // Initialize the database
        executionDataRepository.saveAndFlush(executionData);

        // Get all the executionDatas
        restExecutionDataMockMvc.perform(get("/app/rest/executionData"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].dataId").value(DEFAULT_DATA_ID.intValue()));
    }

    @Test
    @Transactional
    public void getExecutionData() throws Exception {
        // Initialize the database
        executionDataRepository.saveAndFlush(executionData);

        // Get the executionData
        restExecutionDataMockMvc.perform(get("/app/rest/executionData/{id}", executionData.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(executionData.getId().intValue()))
            .andExpect(jsonPath("$.dataId").value(DEFAULT_DATA_ID.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingExecutionData() throws Exception {
        // Get the executionData
        restExecutionDataMockMvc.perform(get("/app/rest/executionData/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateExecutionData() throws Exception {
        // Initialize the database
        executionDataRepository.saveAndFlush(executionData);

        // Update the executionData
        executionData.setDataId(UPDATED_DATA_ID);
        restExecutionDataMockMvc.perform(post("/app/rest/executionData")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(executionData)))
                .andExpect(status().isOk());

        // Validate the ExecutionData in the database
        List<ExecutionData> executionDatas = executionDataRepository.findAll();
        assertThat(executionDatas).hasSize(1);
        ExecutionData testExecutionData = executionDatas.iterator().next();
        assertThat(testExecutionData.getDataId()).isEqualTo(UPDATED_DATA_ID);
    }

    @Test
    @Transactional
    public void deleteExecutionData() throws Exception {
        // Initialize the database
        executionDataRepository.saveAndFlush(executionData);

        // Get the executionData
        restExecutionDataMockMvc.perform(delete("/app/rest/executionData/{id}", executionData.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<ExecutionData> executionDatas = executionDataRepository.findAll();
        assertThat(executionDatas).hasSize(0);
    }
}
