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
import ro.teamnet.scheduler.domain.Task;
import ro.teamnet.scheduler.repository.TaskRepository;
import ro.teamnet.scheduler.service.TaskService;
import ro.teamnet.web.rest.TestUtil;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the TaskResource REST controller.
 *
 * @see TaskResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SchedulerTestApplication.class)
@WebAppConfiguration
@IntegrationTest
@ActiveProfiles("test-scheduler")
public class TaskResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");

    private static final String DEFAULT_TYPE = "SAMPLE_TEXT";
    private static final String UPDATED_TYPE = "UPDATED_TEXT";
    private static final String DEFAULT_QRTZ_JOB_CLASS = "SAMPLE_TEXT";
    private static final String UPDATED_QRTZ_JOB_CLASS = "UPDATED_TEXT";
    private static final String DEFAULT_OPTIONS = "SAMPLE_TEXT";
    private static final String UPDATED_OPTIONS = "UPDATED_TEXT";

    private static final Long DEFAULT_VERSION = 0L;
    private static final Long UPDATED_VERSION = 1L;

    private static final Boolean DEFAULT_DELETED = false;
    private static final Boolean UPDATED_DELETED = true;

    @Inject
    private TaskRepository taskRepository;

    @Inject
    TaskService service;

    private MockMvc restTaskMockMvc;

    private Task task;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TaskResource taskResource = new TaskResource(service);
        this.restTaskMockMvc = MockMvcBuilders.standaloneSetup(taskResource).build();
    }

    @Before
    public void initTest() {
        task = new Task();
        task.setType(DEFAULT_TYPE);
        task.setQuartzJobClassName(DEFAULT_QRTZ_JOB_CLASS);
        task.setOptions(DEFAULT_OPTIONS);
    }

    @Test
    @Transactional
    public void createTask() throws Exception {
        // Validate the database is empty
        assertThat(taskRepository.findAll()).hasSize(0);

        // Create the Task
        restTaskMockMvc.perform(post("/app/rest/task")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(task)))
                .andExpect(status().isOk());

        // Validate the Task in the database
        List<Task> tasks = taskRepository.findAll();
        assertThat(tasks).hasSize(1);
        Task testTask = tasks.iterator().next();
        assertThat(testTask.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testTask.getQuartzJobClassName()).isEqualTo(DEFAULT_QRTZ_JOB_CLASS);
        assertThat(testTask.getOptions()).isEqualTo(DEFAULT_OPTIONS);
        assertThat(testTask.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testTask.getDeleted()).isEqualTo(DEFAULT_DELETED);
    }

    @Test
    @Transactional
    public void getAllTasks() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the tasks
        restTaskMockMvc.perform(get("/app/rest/task"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].type").value(DEFAULT_TYPE.toString()))
                .andExpect(jsonPath("$.[0].quartzJobClassName").value(DEFAULT_QRTZ_JOB_CLASS.toString()))
                .andExpect(jsonPath("$.[0].options").value(DEFAULT_OPTIONS.toString()))
                .andExpect(jsonPath("$.[0].version").value(DEFAULT_VERSION.intValue()))
                .andExpect(jsonPath("$.[0].deleted").value(DEFAULT_DELETED.booleanValue()));
    }

    @Test
    @Transactional
    public void getTask() throws Exception {
        // Initialize the database
        Task task = taskRepository.saveAndFlush(this.task);

        // Get the task
        restTaskMockMvc.perform(get("/app/rest/task/{id}", task.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(task.getId().intValue()))
                .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
                .andExpect(jsonPath("$.quartzJobClassName").value(DEFAULT_QRTZ_JOB_CLASS.toString()))
                .andExpect(jsonPath("$.options").value(DEFAULT_OPTIONS.toString()))
                .andExpect(jsonPath("$.version").value(DEFAULT_VERSION.intValue()))
                .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingTask() throws Exception {
        // Get the task
        restTaskMockMvc.perform(get("/app/rest/task/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTask() throws Exception {
        // Initialize the database
        Task task = taskRepository.saveAndFlush(this.task);

        // Update the task
        task.setType(UPDATED_TYPE);
        task.setQuartzJobClassName(UPDATED_QRTZ_JOB_CLASS);
        task.setOptions(UPDATED_OPTIONS);
        task.setDeleted(UPDATED_DELETED);
        restTaskMockMvc.perform(post("/app/rest/task")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(task)))
                .andExpect(status().isOk());

        // Validate the Task in the database
        List<Task> tasks = taskRepository.findAll();
        assertThat(tasks).hasSize(1);
        Task testTask = tasks.iterator().next();
        assertThat(testTask.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testTask.getQuartzJobClassName()).isEqualTo(UPDATED_QRTZ_JOB_CLASS);
        assertThat(testTask.getOptions()).isEqualTo(UPDATED_OPTIONS);
        assertThat(testTask.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testTask.getDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    public void deleteTask() throws Exception {
        // Initialize the database
        Task task = taskRepository.saveAndFlush(this.task);

        // Get the task
        restTaskMockMvc.perform(delete("/app/rest/task/{id}", task.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Task> tasks = service.findAll();
        assertThat(tasks).hasSize(0);
        List<Task> tasksDeleted = taskRepository.findByDeletedFalse();
        assertThat(tasksDeleted).hasSize(0);
    }
}
