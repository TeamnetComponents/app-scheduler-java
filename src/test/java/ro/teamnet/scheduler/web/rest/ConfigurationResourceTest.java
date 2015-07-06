
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
import ro.teamnet.scheduler.domain.Configuration;
import ro.teamnet.scheduler.repository.ConfigurationRepository;
import ro.teamnet.scheduler.service.ConfigurationService;
import ro.teamnet.web.rest.TestUtil;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ConfigurationResource REST controller.
 *
 * @see ConfigurationResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SchedulerTestApplication.class)
@WebAppConfiguration
@IntegrationTest
@ActiveProfiles("test-scheduler")
public class ConfigurationResourceTest {

    private static final Long DEFAULT_CONFIGURATION_ID = 0L;
    private static final Long UPDATED_CONFIGURATION_ID = 1L;
    private static final String DEFAULT_TYPE = "DEFAULT_TYPE";
    private static final String UPDATED_TYPE = "UPDATED_TYPE";

    @Inject
    private ConfigurationRepository configurationRepository;

    @Inject
    ConfigurationService service;

    private MockMvc restConfigurationMockMvc;

    private Configuration configuration;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ConfigurationResource configurationResource = new ConfigurationResource(service);
        this.restConfigurationMockMvc = MockMvcBuilders.standaloneSetup(configurationResource).build();
    }

    @Before
    public void initTest() {
        configuration = new Configuration();
        configuration.setConfigurationId(DEFAULT_CONFIGURATION_ID);
        configuration.setType(DEFAULT_TYPE);
    }

    @Test
    @Transactional
    public void createConfiguration() throws Exception {
        // Validate the database is empty
        assertThat(configurationRepository.findAll()).hasSize(0);

        // Create the Configuration
        restConfigurationMockMvc.perform(post("/app/rest/configuration")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(configuration)))
                .andExpect(status().isOk());

        // Validate the Configuration in the database
        List<Configuration> configurations = configurationRepository.findAll();
        assertThat(configurations).hasSize(1);
        Configuration testConfiguration = configurations.iterator().next();
        assertThat(testConfiguration.getConfigurationId()).isEqualTo(DEFAULT_CONFIGURATION_ID);
        assertThat(testConfiguration.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    @Transactional
    public void getAllConfigurations() throws Exception {
        // Initialize the database
        configurationRepository.saveAndFlush(configuration);

        // Get all the configurations
        restConfigurationMockMvc.perform(get("/app/rest/configuration"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].configurationId").value(DEFAULT_CONFIGURATION_ID.intValue()))
                .andExpect(jsonPath("$.[0].type").value(DEFAULT_TYPE));
    }

    @Test
    @Transactional
    public void getConfiguration() throws Exception {
        // Initialize the database
        configurationRepository.saveAndFlush(configuration);

        // Get the configuration
        restConfigurationMockMvc.perform(get("/app/rest/configuration/{id}", configuration.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(configuration.getId().intValue()))
                .andExpect(jsonPath("$.configurationId").value(DEFAULT_CONFIGURATION_ID.intValue()))
                .andExpect(jsonPath("$.type").value(DEFAULT_TYPE));
    }

    @Test
    @Transactional
    public void getNonExistingConfiguration() throws Exception {
        // Get the configuration
        restConfigurationMockMvc.perform(get("/app/rest/configuration/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateConfiguration() throws Exception {
        // Initialize the database
        configurationRepository.saveAndFlush(configuration);

        // Update the configuration
        configuration.setConfigurationId(UPDATED_CONFIGURATION_ID);
        configuration.setType(UPDATED_TYPE);
        restConfigurationMockMvc.perform(post("/app/rest/configuration")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(configuration)))
                .andExpect(status().isOk());

        // Validate the Configuration in the database
        List<Configuration> configurations = configurationRepository.findAll();
        assertThat(configurations).hasSize(1);
        Configuration testConfiguration = configurations.iterator().next();
        assertThat(testConfiguration.getConfigurationId()).isEqualTo(UPDATED_CONFIGURATION_ID);
        assertThat(testConfiguration.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void deleteConfiguration() throws Exception {
        // Initialize the database
        configurationRepository.saveAndFlush(configuration);

        // Get the configuration
        restConfigurationMockMvc.perform(delete("/app/rest/configuration/{id}", configuration.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Configuration> configurations = configurationRepository.findAll();
        assertThat(configurations).hasSize(0);
    }
}
