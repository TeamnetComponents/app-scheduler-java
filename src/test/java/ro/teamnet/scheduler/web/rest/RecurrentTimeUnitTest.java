package ro.teamnet.scheduler.web.rest;


import junit.framework.Assert;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ro.teamnet.SchedulerTestApplication;
import ro.teamnet.scheduler.domain.RecurrentTimeUnit;
import ro.teamnet.scheduler.domain.Schedule;
import ro.teamnet.scheduler.domain.TimeUnit;
import ro.teamnet.scheduler.repository.ScheduleRepository;
import ro.teamnet.scheduler.service.RecurrentTimeUnitService;
import ro.teamnet.scheduler.service.ScheduleService;
import ro.teamnet.scheduler.service.TimeUnitService;
import ro.teamnet.web.rest.TestUtil;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SchedulerTestApplication.class)
@WebAppConfiguration
@IntegrationTest
@ActiveProfiles("test-postgres")
public class RecurrentTimeUnitTest {

    @Inject
    private EntityManagerFactory emf;

    private final Logger logger = LoggerFactory.getLogger(RecurrentTimeUnitTest.class);

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean DEFAULT_RECURRENT = false;
//    private static final DateTime DEFAULT_START_TIME = new DateTime();
    private static final String dateTime = "2015-06-30T03:30:00";
    private static final DateTime DEFAULT_START_TIME = dateTimeFormatter.parseDateTime(dateTime);

    private static final DateTime DEFAULT_END_TIME = new DateTime(0L);
    private static final Long DEFAULT_REPETITIONS = 0L;
    private static final Long DEFAULT_VERSION = 0L;
    private static final Boolean DEFAULT_DELETED = false;

    private static final Boolean UPDATED_ACTIVE = true;
    private static final Boolean UPDATED_RECURRENT = true;
    private static final DateTime UPDATED_START_TIME = DEFAULT_START_TIME;
    private static final DateTime UPDATED_END_TIME = DEFAULT_END_TIME;
    private static final Long UPDATED_REPETITIONS = DEFAULT_REPETITIONS;
    private static final Long UPDATED_VERSION = 1L;
    private static final Boolean UPDATED_DELETED = true;

    @Inject
    private ScheduleRepository scheduleRepository;

    @Inject
    private ScheduleService service;

    @Inject
    private TimeUnitService timeUnitService;

    @Inject
    private RecurrentTimeUnitService recurrentTimeUnitService;

    private MockMvc restScheduleMockMvc;

    private Schedule schedule;

    @PostConstruct
    @Ignore
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ScheduleResource scheduleResource = new ScheduleResource(service);
        this.restScheduleMockMvc = MockMvcBuilders.standaloneSetup(scheduleResource).build();
    }

    @Before
    @Ignore
    public void initTest() {
        schedule = new Schedule();
        schedule.setId(130L);
        schedule.setActive(DEFAULT_ACTIVE);
        schedule.setRecurrent(DEFAULT_RECURRENT);
        schedule.setStartTime(DEFAULT_START_TIME);
        schedule.setEndTime(DEFAULT_END_TIME);
        schedule.setRepetitions(DEFAULT_REPETITIONS);
    }

//    @Test
//    public void test() {
//        TimeUnit timeUnit1 = new TimeUnit();
//        timeUnit1.setCode("A");
//        TimeUnit timeUnit2 = new TimeUnit();
//        timeUnit2.setCode("B");
//        TimeUnit timeUnit3 = new TimeUnit();
//        timeUnit3.setCode("C");
//        TimeUnit timeUnit4 = new TimeUnit();
//        timeUnit4.setCode("D");
//        ArrayList<TimeUnit> myArray = new ArrayList<>();
//        myArray.add(timeUnit1);
//        myArray.add(timeUnit2);
//        myArray.add(timeUnit3);
//        myArray.add(timeUnit4);
//
//        String[] s1 = new String[]{"First code","Second code","Third code","Fourth code"};
//        int i = 0;
//        for(TimeUnit timeUnit : myArray) {
//            logger.info(s1[i++]);
//            logger.info("~~~~~~~~~~~~~~~~~~~~~~~");
//            logger.info(timeUnit.getCode());
//            logger.info("~~~~~~~~~~~~~~~~~~~~~~~");
//        }
//    }

    @Test
    @Transactional
    @Ignore
    public void createScheduleWithRecurrentTimeUnits() throws Exception {


        int initialSize = scheduleRepository.findAll().size();//0

        Schedule schedule1 = this.schedule;
        add4RecurrentTimeUnits(schedule1);

        // Create the Schedule
        restScheduleMockMvc.perform(post("/app/rest/schedule")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(schedule1)))
                .andExpect(status().isOk());

        // Validate the Schedule in the database
        List<Schedule> schedules = scheduleRepository.findAll();//1
        assertThat(schedules).hasSize(initialSize + 1);
        Schedule testSchedule = schedules.get(initialSize);
        //Schedule testSchedule = schedules.iterator().next();
//        assertThat(testSchedule.getActive()).isEqualTo(DEFAULT_ACTIVE);
//        assertThat(testSchedule.getRecurrent()).isEqualTo(DEFAULT_RECURRENT);
//        assertThat(testSchedule.getCron()).endsWith("30 6 ? 2015");
//        assertThat(testSchedule.getStartTime()).isEqualTo(DEFAULT_START_TIME);
//        assertThat(testSchedule.getEndTime()).isEqualTo(DEFAULT_END_TIME);
//        assertThat(testSchedule.getRepetitions()).isEqualTo(DEFAULT_REPETITIONS);
//        assertThat(testSchedule.getVersion()).isEqualTo(DEFAULT_VERSION);
//        assertThat(testSchedule.getDeleted()).isEqualTo(DEFAULT_DELETED);
//        assertThat(testSchedule.getRecurrentTimeUnits().size()).isEqualTo(4);

        List<String> myStringList = Arrays.asList("MON","D","W","H");
        List<Integer> myIntegerList = Arrays.asList(1,1,1,23);
        int index = 0;
        Iterator<RecurrentTimeUnit> iterator = testSchedule.getRecurrentTimeUnits().iterator();
        logger.info(String.valueOf(testSchedule.getRecurrentTimeUnits().size()));
        while(iterator.hasNext()) {
            RecurrentTimeUnit recurrentTimeUnit = iterator.next();
//            assertThat(recurrentTimeUnit.getTimeUnit().getCode()).isEqualTo(myStringList.get(index));
//            assertThat(recurrentTimeUnit.getValue()).isEqualTo(myIntegerList.get(index++));
            logger.info(recurrentTimeUnit.getTimeUnit().getCode());
        }
    }

    @Test
    @Ignore
//    @Transactional
    public void updateScheduleWithRecurrentTimeUnits() throws Exception {

        Schedule scheduleA = getSchedule();
        test(scheduleA);


    }
    @Transactional
    @Ignore
    private void test(Schedule scheduleA) {
        Schedule testSchedule = scheduleRepository.findOne(scheduleA.getId());
//        List<Schedule> schedules = scheduleRepository.findAll();
//        assertThat(schedules).hasSize(8);
//        assertThat(testSchedule.getActive()).isEqualTo(UPDATED_ACTIVE);
//        assertThat(testSchedule.getRecurrent()).isEqualTo(UPDATED_RECURRENT);
//        assertThat(testSchedule.getCron()).isNotNull();
//        assertThat(testSchedule.getCron()).isNotEqualTo(initialCron);
//        assertThat(testSchedule.getStartTime()).isEqualTo(UPDATED_START_TIME);
//        assertThat(testSchedule.getEndTime()).isEqualTo(UPDATED_END_TIME);
//        assertThat(testSchedule.getRepetitions()).isEqualTo(UPDATED_REPETITIONS);
//        assertThat(testSchedule.getVersion()).isEqualTo(UPDATED_VERSION);
//        assertThat(testSchedule.getDeleted()).isEqualTo(UPDATED_DELETED);
        /**
         * Verify number of RTUs of created schedule
         */
        Assert.assertEquals("schedule @ update", testSchedule.getRecurrentTimeUnits().size(), 3);
        Set<RecurrentTimeUnit> recurrentTimeUnits2 = testSchedule.getRecurrentTimeUnits();
        int j = 0;
        String[] s2 = new String[]{"First","Second","Third"};
        for(RecurrentTimeUnit rtu : recurrentTimeUnits2) {
            logger.info(s2[j++]);
            logger.info("~~~~~~~~~~~~~~~~~~~~~~~");
            logger.info(rtu.getTimeUnit().getCode());
            logger.info(rtu.getTimeUnit().getDescription());
            logger.info(String.valueOf(rtu.getValue()));
            logger.info("~~~~~~~~~~~~~~~~~~~~~~~");
        }
    }
    @Transactional
    @Ignore
    private Schedule getSchedule() throws Exception {
        /**
         * Create schedule (@Before -> setting some properties)
         */
        Schedule schedule1 = this.schedule;
        /**
         * Setting RecurrentTimeUnits
         */
        add4RecurrentTimeUnits(schedule1);
        /**
         * Save schedule to DB
         */
        scheduleRepository.save(schedule1);
        /**
         * Find saved schedule by Id
         */
        Schedule scheduleA = scheduleRepository.findOne(schedule1.getId());
        /**
         * Verify number of RTUs of created schedule
         */
        Assert.assertEquals("schedule @ save", schedule1.getRecurrentTimeUnits().size(), 4);
        logger.info(String.valueOf(schedule1.getRecurrentTimeUnits().size()));
        Set<RecurrentTimeUnit> recurrentTimeUnits1 = schedule1.getRecurrentTimeUnits();
        String[] s1 = new String[]{"First","Second","Third","Fourth"};
        int i = 0;
        for(RecurrentTimeUnit rtu : recurrentTimeUnits1) {
            logger.info(s1[i++]);
            logger.info("~~~~~~~~~~~~~~~~~~~~~~~");
            logger.info(rtu.getTimeUnit().getCode());
            logger.info(rtu.getTimeUnit().getDescription());
            logger.info(String.valueOf(rtu.getValue()));
            logger.info("~~~~~~~~~~~~~~~~~~~~~~~");
        }


        String initialCron = schedule1.getCron();
        scheduleA.setActive(UPDATED_ACTIVE);
        scheduleA.setRecurrent(UPDATED_RECURRENT);
        scheduleA.setStartTime(UPDATED_START_TIME);
        scheduleA.setEndTime(UPDATED_END_TIME);
        scheduleA.setRepetitions(UPDATED_REPETITIONS);
        scheduleA.setDeleted(UPDATED_DELETED);

        /**
         * Setting new RecurrentTimeUnits (update)
         */
        addNew3RecurrentTimeUnits(scheduleA);
        /**
         * Update schedule to DB
         */
        restScheduleMockMvc.perform(post("/app/rest/schedule")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(scheduleA)))
                .andExpect(status().isOk());
        /**
         * Find updated schedule by Id
         */return scheduleA;
    }


    @Test
    @Transactional
    @Ignore
    public void deleteSchedule() throws Exception {

        Schedule schedule = scheduleRepository.saveAndFlush(this.schedule);

        restScheduleMockMvc.perform(delete("/app/rest/schedule/{id}", schedule.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());


        int initialSize = scheduleRepository.findAll().size();
        List<Schedule> schedulesNotDeleted = scheduleRepository.findByDeletedFalse();
        assertThat(schedulesNotDeleted).hasSize(initialSize - 1);
    }

    @Ignore
    private void add4RecurrentTimeUnits(Schedule schedule) {
        Set<RecurrentTimeUnit> recurrentTimeUnits = new HashSet<>();

        RecurrentTimeUnit rtu1 = new RecurrentTimeUnit();

        rtu1.setValue(4);
        rtu1.setTimeUnit(timeUnitService.findByCode("MON"));
        recurrentTimeUnits.add(rtu1);

        RecurrentTimeUnit rtu2 = new RecurrentTimeUnit();

        rtu2.setValue(4);
        rtu2.setTimeUnit(timeUnitService.findByCode("D"));
        recurrentTimeUnits.add(rtu2);

        RecurrentTimeUnit rtu3 = new RecurrentTimeUnit();

        rtu3.setValue(4);
        rtu3.setTimeUnit(timeUnitService.findByCode("W"));
        recurrentTimeUnits.add(rtu3);

        RecurrentTimeUnit rtu4 = new RecurrentTimeUnit();

        rtu4.setValue(23);
        rtu4.setTimeUnit(timeUnitService.findByCode("H"));
        recurrentTimeUnits.add(rtu4);

        schedule.setRecurrentTimeUnits(recurrentTimeUnits);

//        for(RecurrentTimeUnit recurrentTimeUnit : schedule.getRecurrentTimeUnits()) {
//            recurrentTimeUnit.setSchedule(schedule);
//        }
    }

    @Ignore
    private void addNew3RecurrentTimeUnits(Schedule schedule) {
        Set<RecurrentTimeUnit> recurrentTimeUnits = new HashSet<>();

        RecurrentTimeUnit rtu1 = new RecurrentTimeUnit();

        rtu1.setValue(2);
        rtu1.setTimeUnit(timeUnitService.findByCode("MON"));
        recurrentTimeUnits.add(rtu1);

        RecurrentTimeUnit rtu2 = new RecurrentTimeUnit();

        rtu2.setValue(2);
        rtu2.setTimeUnit(timeUnitService.findByCode("D"));
        recurrentTimeUnits.add(rtu2);

        RecurrentTimeUnit rtu3 = new RecurrentTimeUnit();

        rtu3.setValue(2);
        rtu3.setTimeUnit(timeUnitService.findByCode("W"));
        recurrentTimeUnits.add(rtu3);

        schedule.setRecurrentTimeUnits(recurrentTimeUnits);

        for(RecurrentTimeUnit recurrentTimeUnit : schedule.getRecurrentTimeUnits()) {
            recurrentTimeUnit.setSchedule(schedule);
        }
    }
    @Ignore
    private TimeUnit createTimeUnit(String code) {
        TimeUnit timeUnit = new TimeUnit();
        timeUnit.setCode(code);

        return timeUnit;
    }


}
