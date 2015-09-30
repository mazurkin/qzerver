package org.qzerver.model.service.job.management.impl;

import com.gainmatrix.lib.business.entity.BusinessEntityDao;
import com.gainmatrix.lib.paging.Extraction;
import com.gainmatrix.lib.time.impl.StubChronometer;
import com.google.common.collect.Iterators;
import junit.framework.Assert;
import org.easymock.Capture;
import org.easymock.CaptureType;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;
import org.qzerver.base.AbstractTransactionalTest;
import org.qzerver.model.dao.job.ScheduleActionDao;
import org.qzerver.model.dao.job.ScheduleExecutionDao;
import org.qzerver.model.dao.job.ScheduleGroupDao;
import org.qzerver.model.dao.job.ScheduleJobDao;
import org.qzerver.model.domain.entities.cluster.ClusterGroup;
import org.qzerver.model.domain.entities.cluster.ClusterNode;
import org.qzerver.model.domain.entities.job.ScheduleAction;
import org.qzerver.model.domain.entities.job.ScheduleExecution;
import org.qzerver.model.domain.entities.job.ScheduleExecutionStatus;
import org.qzerver.model.domain.entities.job.ScheduleExecutionStrategy;
import org.qzerver.model.domain.entities.job.ScheduleGroup;
import org.qzerver.model.domain.entities.job.ScheduleJob;
import org.qzerver.model.service.job.management.dto.ScheduleJobActionParameters;
import org.qzerver.model.service.job.management.dto.ScheduleJobCreateParameters;
import org.qzerver.model.service.job.management.dto.ScheduleJobModifyParameters;
import org.qzerver.model.service.job.management.dto.ScheduleJobRescheduleParameters;
import org.qzerver.model.service.quartz.management.QuartzManagementService;
import org.springframework.validation.Validator;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Arrays;
import java.util.List;

public class ScheduleJobManagementServiceImplTest extends AbstractTransactionalTest {

    private static final String DEFAULT_TIMEZONE = "UTC";

    private IMocksControl control;

    private ScheduleJobManagementServiceImpl scheduleJobManagementService;

    private QuartzManagementService quartzManagementService;

    @Resource
    private Validator modelBeanValidator;

    @Resource
    private BusinessEntityDao businessEntityDao;

    @Resource
    private StubChronometer chronometer;

    @Resource
    private ScheduleExecutionDao scheduleExecutionDao;

    @Resource
    private ScheduleJobDao scheduleJobDao;

    @Resource
    private ScheduleGroupDao scheduleGroupDao;

    @Resource
    private ScheduleActionDao scheduleActionDao;

    @PersistenceContext
    private EntityManager entityManager;

    @Before
    public void setUp() throws Exception {
        control = EasyMock.createStrictControl();

        quartzManagementService = control.createMock(QuartzManagementService.class);

        scheduleJobManagementService  = new ScheduleJobManagementServiceImpl();
        scheduleJobManagementService.setQuartzManagementService(quartzManagementService);
        scheduleJobManagementService.setBeanValidator(modelBeanValidator);
        scheduleJobManagementService.setBusinessEntityDao(businessEntityDao);
        scheduleJobManagementService.setChronometer(chronometer);
        scheduleJobManagementService.setScheduleExecutionDao(scheduleExecutionDao);
        scheduleJobManagementService.setScheduleGroupDao(scheduleGroupDao);
        scheduleJobManagementService.setScheduleActionDao(scheduleActionDao);
    }

    @Test
    public void testGroups() {
        // Create group

        ScheduleGroup scheduleGroup = scheduleJobManagementService.createGroup("test group");
        Assert.assertNotNull(scheduleGroup);

        entityManager.flush();
        entityManager.clear();

        ScheduleGroup scheduleGroupModified;

        scheduleGroupModified = scheduleJobManagementService.findGroup(scheduleGroup.getId());
        Assert.assertNotNull(scheduleGroupModified);
        Assert.assertEquals(scheduleGroup, scheduleGroupModified);

        List<ScheduleGroup> scheduleGroups = scheduleJobManagementService.findAllGroups(Extraction.ALL);
        Assert.assertNotNull(scheduleGroups);
        Assert.assertTrue(scheduleGroups.size() > 0);
        Assert.assertTrue(Iterators.contains(scheduleGroups.iterator(), scheduleGroupModified));

        // Modify group

        scheduleGroupModified = scheduleJobManagementService.modifyGroup(scheduleGroup.getId(), "Name 2");
        Assert.assertNotNull(scheduleGroupModified);

        entityManager.flush();
        entityManager.clear();

        scheduleGroupModified = scheduleJobManagementService.findGroup(scheduleGroup.getId());
        Assert.assertNotNull(scheduleGroupModified);
        Assert.assertEquals("Name 2", scheduleGroupModified.getName());

        // Delete group

        scheduleJobManagementService.deleteGroup(scheduleGroup.getId());

        scheduleGroupModified = scheduleJobManagementService.findGroup(scheduleGroup.getId());
        Assert.assertNull(scheduleGroupModified);
    }

    @Test
    public void testCreateJobWithoutCluster() throws Exception {
        String cron = "0 0 0 * * ?";

        ScheduleGroup scheduleGroup = scheduleJobManagementService.createGroup("test group");
        Assert.assertNotNull(scheduleGroup);

        control.reset();

        quartzManagementService.createJob(
            EasyMock.anyLong(),
            EasyMock.eq(cron),
            EasyMock.eq(DEFAULT_TIMEZONE),
            EasyMock.eq(true)
        );

        control.replay();

        ScheduleJobCreateParameters jobParameters = new ScheduleJobCreateParameters();
        jobParameters.setName("Test Job");
        jobParameters.setDescription("Nothing to do");
        jobParameters.setTimezone(DEFAULT_TIMEZONE);
        jobParameters.setCron(cron);
        jobParameters.setEnabled(true);
        jobParameters.setClusterGroupId(null);
        jobParameters.setScheduleGroupId(scheduleGroup.getId());
        jobParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);
        jobParameters.setActionIdentifier("action.type");
        jobParameters.setActionDefinition("action.data".getBytes());

        ScheduleJob scheduleJob = scheduleJobManagementService.createJob(jobParameters);
        Assert.assertNotNull(scheduleJob);

        control.verify();

        ScheduleAction action = scheduleJob.getAction();
        Assert.assertNotNull(action);
    }

    @Test
    public void testCreateJobWithCluster() throws Exception {
        String cron = "0 0 0 * * ?";

        ScheduleGroup scheduleGroup = scheduleJobManagementService.createGroup("test group");
        Assert.assertNotNull(scheduleGroup);

        ClusterGroup clusterGroup = new ClusterGroup();
        clusterGroup.setName("Test cluster group");
        businessEntityDao.save(clusterGroup);

        ClusterNode clusterNode1 = new ClusterNode();
        clusterNode1.setAddress("10.2.0.1");
        clusterNode1.setDescription("test node");
        clusterNode1.setEnabled(true);
        clusterNode1.setGroup(clusterGroup);
        clusterGroup.getNodes().add(clusterNode1);

        ClusterNode clusterNode2 = new ClusterNode();
        clusterNode2.setAddress("10.2.0.2");
        clusterNode2.setDescription("test node");
        clusterNode2.setEnabled(true);
        clusterNode2.setGroup(clusterGroup);
        clusterGroup.getNodes().add(clusterNode2);

        control.reset();

        quartzManagementService.createJob(
            EasyMock.anyLong(),
            EasyMock.eq(cron),
            EasyMock.eq(DEFAULT_TIMEZONE),
            EasyMock.eq(true)
        );

        control.replay();

        ScheduleJobCreateParameters jobParameters = new ScheduleJobCreateParameters();
        jobParameters.setName("Test Job");
        jobParameters.setDescription("Nothing to do");
        jobParameters.setTimezone(DEFAULT_TIMEZONE);
        jobParameters.setCron(cron);
        jobParameters.setEnabled(true);
        jobParameters.setClusterGroupId(clusterGroup.getId());
        jobParameters.setScheduleGroupId(scheduleGroup.getId());
        jobParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);
        jobParameters.setActionIdentifier("action.type");
        jobParameters.setActionDefinition("action.data".getBytes());

        ScheduleJob scheduleJob = scheduleJobManagementService.createJob(jobParameters);
        Assert.assertNotNull(scheduleJob);

        control.verify();

        ScheduleAction action = scheduleJob.getAction();
        Assert.assertNotNull(action);
    }

    @Test
    public void testDeleteJob() throws Exception {
        String cron = "0 0 0 * * ?";

        ScheduleGroup scheduleGroup = scheduleJobManagementService.createGroup("test group");
        Assert.assertNotNull(scheduleGroup);

        ClusterGroup clusterGroup = new ClusterGroup();
        clusterGroup.setName("Test cluster group");
        businessEntityDao.save(clusterGroup);

        ClusterNode clusterNode = new ClusterNode();
        clusterNode.setAddress("10.2.0.1");
        clusterNode.setDescription("test node");
        clusterNode.setEnabled(true);
        clusterNode.setGroup(clusterGroup);
        clusterGroup.getNodes().add(clusterNode);

        Capture<Long> idCapture1 = new Capture<Long>(CaptureType.ALL);
        Capture<Long> idCapture2 = new Capture<Long>(CaptureType.ALL);

        control.reset();

        quartzManagementService.createJob(
            EasyMock.capture(idCapture1),
            EasyMock.eq(cron),
            EasyMock.eq(DEFAULT_TIMEZONE),
            EasyMock.eq(true)
        );

        quartzManagementService.deleteJob(
            EasyMock.capture(idCapture2)
        );

        control.replay();

        ScheduleJobCreateParameters jobParameters = new ScheduleJobCreateParameters();
        jobParameters.setName("Test Job");
        jobParameters.setDescription("Nothing to do");
        jobParameters.setTimezone(DEFAULT_TIMEZONE);
        jobParameters.setCron(cron);
        jobParameters.setEnabled(true);
        jobParameters.setClusterGroupId(clusterGroup.getId());
        jobParameters.setScheduleGroupId(scheduleGroup.getId());
        jobParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);
        jobParameters.setActionIdentifier("action.type");
        jobParameters.setActionDefinition("action.data".getBytes());

        ScheduleJob scheduleJob = scheduleJobManagementService.createJob(jobParameters);
        Assert.assertNotNull(scheduleJob);

        ScheduleExecution scheduleExecution = new ScheduleExecution();
        scheduleExecution.setJob(scheduleJob);
        scheduleExecution.setAction(scheduleJob.getAction());
        scheduleExecution.setCron(scheduleJob.getCron());
        scheduleExecution.setName(scheduleJob.getName());
        scheduleExecution.setStrategy(scheduleJob.getStrategy());
        scheduleExecution.setTimeout(scheduleJob.getTimeout());
        scheduleExecution.setAllNodes(scheduleJob.isAllNodes());
        scheduleExecution.setScheduled(chronometer.getCurrentMoment());
        scheduleExecution.setFired(chronometer.getCurrentMoment());
        scheduleExecution.setForced(false);
        scheduleExecution.setDescription("Test execution");
        scheduleExecution.setStatus(ScheduleExecutionStatus.INPROGRESS);
        scheduleExecution.setStarted(chronometer.getCurrentMoment());
        scheduleExecution.setFinished(null);
        scheduleExecution.setHostname("localhost");
        businessEntityDao.save(scheduleExecution);

        scheduleJobManagementService.deleteJob(scheduleJob.getId());

        control.verify();

        Assert.assertEquals(scheduleJob.getId(), idCapture1.getValue());
        Assert.assertEquals(scheduleJob.getId(), idCapture2.getValue());

        ScheduleAction action = scheduleJob.getAction();
        Assert.assertNotNull(action);

        entityManager.flush();
        entityManager.clear();

        scheduleExecution = businessEntityDao.findById(ScheduleExecution.class, scheduleExecution.getId());
        Assert.assertNotNull(scheduleExecution);
        Assert.assertNotNull(scheduleExecution.getAction());
        Assert.assertNull(scheduleExecution.getJob());
    }

    @Test
    public void testModifyJob() throws Exception {
        String cron = "0 0 0 * * ?";

        ScheduleGroup scheduleGroup = scheduleJobManagementService.createGroup("test group");
        Assert.assertNotNull(scheduleGroup);

        ClusterGroup clusterGroup = new ClusterGroup();
        clusterGroup.setName("Test cluster group");
        businessEntityDao.save(clusterGroup);

        ClusterNode clusterNode = new ClusterNode();
        clusterNode.setAddress("10.2.0.1");
        clusterNode.setDescription("test node");
        clusterNode.setEnabled(true);
        clusterNode.setGroup(clusterGroup);
        clusterGroup.getNodes().add(clusterNode);

        control.reset();

        quartzManagementService.createJob(
            EasyMock.anyLong(),
            EasyMock.eq(cron),
            EasyMock.eq(DEFAULT_TIMEZONE),
            EasyMock.eq(true)
        );

        control.replay();

        ScheduleJobCreateParameters jobParameters = new ScheduleJobCreateParameters();
        jobParameters.setName("Test Job");
        jobParameters.setDescription("Nothing to do");
        jobParameters.setTimezone(DEFAULT_TIMEZONE);
        jobParameters.setCron(cron);
        jobParameters.setEnabled(true);
        jobParameters.setClusterGroupId(clusterGroup.getId());
        jobParameters.setScheduleGroupId(scheduleGroup.getId());
        jobParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);
        jobParameters.setActionIdentifier("action.type");
        jobParameters.setActionDefinition("action.data".getBytes());

        ScheduleJob scheduleJob = scheduleJobManagementService.createJob(jobParameters);
        Assert.assertNotNull(scheduleJob);

        ScheduleJobModifyParameters modifyParameters = new ScheduleJobModifyParameters();
        modifyParameters.setName("Name2");
        modifyParameters.setDescription("Description2");
        modifyParameters.setAllNodes(true);
        modifyParameters.setNotifyOnFailure(false);
        modifyParameters.setTimeout(1000);
        modifyParameters.setNodesLimit(4);

        ScheduleJob scheduleJobModified = scheduleJobManagementService.modifyJob(scheduleJob.getId(), modifyParameters);
        Assert.assertNotNull(scheduleJobModified);
        Assert.assertEquals("Name2", scheduleJobModified.getName());
        Assert.assertEquals("Description2", scheduleJobModified.getDescription());
        Assert.assertEquals(true, scheduleJobModified.isAllNodes());
        Assert.assertEquals(false, scheduleJobModified.isNotifyOnFailure());
        Assert.assertEquals(1000, scheduleJobModified.getTimeout());
        Assert.assertEquals(4, scheduleJobModified.getNodesLimit());

        control.verify();
    }

    @Test
    public void testToggleJob() throws Exception {
        String cron = "0 0 0 * * ?";

        ScheduleGroup scheduleGroup = scheduleJobManagementService.createGroup("test group");
        Assert.assertNotNull(scheduleGroup);

        ClusterGroup clusterGroup = new ClusterGroup();
        clusterGroup.setName("Test cluster group");
        businessEntityDao.save(clusterGroup);

        ClusterNode clusterNode = new ClusterNode();
        clusterNode.setAddress("10.2.0.1");
        clusterNode.setDescription("test node");
        clusterNode.setEnabled(true);
        clusterNode.setGroup(clusterGroup);
        clusterGroup.getNodes().add(clusterNode);

        Capture<Long> idCapture1 = new Capture<Long>(CaptureType.ALL);
        Capture<Long> idCapture2 = new Capture<Long>(CaptureType.ALL);

        control.reset();

        quartzManagementService.createJob(
            EasyMock.anyLong(),
            EasyMock.eq(cron),
            EasyMock.eq(DEFAULT_TIMEZONE),
            EasyMock.eq(true)
        );

        quartzManagementService.disableJob(
            EasyMock.capture(idCapture1)
        );

        quartzManagementService.enableJob(
            EasyMock.capture(idCapture2),
            EasyMock.eq(cron),
            EasyMock.eq(DEFAULT_TIMEZONE)
        );

        control.replay();

        ScheduleJobCreateParameters jobParameters = new ScheduleJobCreateParameters();
        jobParameters.setName("Test Job");
        jobParameters.setDescription("Nothing to do");
        jobParameters.setTimezone(DEFAULT_TIMEZONE);
        jobParameters.setCron(cron);
        jobParameters.setEnabled(true);
        jobParameters.setClusterGroupId(clusterGroup.getId());
        jobParameters.setScheduleGroupId(scheduleGroup.getId());
        jobParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);
        jobParameters.setActionIdentifier("action.type");
        jobParameters.setActionDefinition("action.data".getBytes());

        ScheduleJob scheduleJob = scheduleJobManagementService.createJob(jobParameters);
        Assert.assertNotNull(scheduleJob);

        ScheduleJob scheduleJobModified;

        scheduleJobModified = scheduleJobManagementService.disableJob(scheduleJob.getId());
        Assert.assertNotNull(scheduleJobModified);
        Assert.assertFalse(scheduleJobModified.isEnabled());

        scheduleJobModified = scheduleJobManagementService.enableJob(scheduleJob.getId());
        Assert.assertNotNull(scheduleJobModified);
        Assert.assertTrue(scheduleJobModified.isEnabled());

        control.verify();

        Assert.assertEquals(scheduleJobModified.getId(), idCapture1.getValue());
        Assert.assertEquals(scheduleJobModified.getId(), idCapture2.getValue());
    }

    @Test
    public void testRescheduleActiveJob() throws Exception {
        String cron1 = "0 0 0 * * ?";
        String cron2 = "1 0 0 * * ?";

        ScheduleGroup scheduleGroup = scheduleJobManagementService.createGroup("test group");
        Assert.assertNotNull(scheduleGroup);

        ClusterGroup clusterGroup = new ClusterGroup();
        clusterGroup.setName("Test cluster group");
        businessEntityDao.save(clusterGroup);

        ClusterNode clusterNode = new ClusterNode();
        clusterNode.setAddress("10.2.0.1");
        clusterNode.setDescription("test node");
        clusterNode.setEnabled(true);
        clusterNode.setGroup(clusterGroup);
        clusterGroup.getNodes().add(clusterNode);

        Capture<Long> idCapture = new Capture<Long>(CaptureType.ALL);

        control.reset();

        quartzManagementService.createJob(
            EasyMock.anyLong(),
            EasyMock.eq(cron1),
            EasyMock.eq(DEFAULT_TIMEZONE),
            EasyMock.eq(true)
        );

        quartzManagementService.rescheduleJob(
            EasyMock.capture(idCapture),
            EasyMock.eq(cron2),
            EasyMock.eq(DEFAULT_TIMEZONE)
        );

        control.replay();

        ScheduleJobCreateParameters jobParameters = new ScheduleJobCreateParameters();
        jobParameters.setName("Test Job");
        jobParameters.setDescription("Nothing to do");
        jobParameters.setTimezone(DEFAULT_TIMEZONE);
        jobParameters.setCron(cron1);
        jobParameters.setEnabled(true);
        jobParameters.setClusterGroupId(clusterGroup.getId());
        jobParameters.setScheduleGroupId(scheduleGroup.getId());
        jobParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);
        jobParameters.setActionIdentifier("action.type");
        jobParameters.setActionDefinition("action.data".getBytes());

        ScheduleJob scheduleJob = scheduleJobManagementService.createJob(jobParameters);
        Assert.assertNotNull(scheduleJob);

        ScheduleJobRescheduleParameters rescheduleParameters = new ScheduleJobRescheduleParameters();
        rescheduleParameters.setCron(cron2);
        rescheduleParameters.setTimezone(DEFAULT_TIMEZONE);

        ScheduleJob scheduleJobModified;

        scheduleJobModified = scheduleJobManagementService.rescheduleJob(scheduleJob.getId(), rescheduleParameters);
        Assert.assertNotNull(scheduleJobModified);
        Assert.assertTrue(scheduleJobModified.isEnabled());

        control.verify();

        Assert.assertEquals(scheduleJobModified.getId(), idCapture.getValue());
    }

    @Test
    public void testChangeJobActionTest() throws Exception {
        String cron = "0 0 0 * * ?";

        ScheduleGroup scheduleGroup = scheduleJobManagementService.createGroup("test group");
        Assert.assertNotNull(scheduleGroup);

        ClusterGroup clusterGroup = new ClusterGroup();
        clusterGroup.setName("Test cluster group");
        businessEntityDao.save(clusterGroup);

        ClusterNode clusterNode = new ClusterNode();
        clusterNode.setAddress("10.2.0.1");
        clusterNode.setDescription("test node");
        clusterNode.setEnabled(true);
        clusterNode.setGroup(clusterGroup);
        clusterGroup.getNodes().add(clusterNode);

        control.reset();

        quartzManagementService.createJob(
            EasyMock.anyLong(),
            EasyMock.eq(cron),
            EasyMock.eq(DEFAULT_TIMEZONE),
            EasyMock.eq(true)
        );

        control.replay();

        ScheduleJobCreateParameters jobParameters = new ScheduleJobCreateParameters();
        jobParameters.setName("Test Job");
        jobParameters.setDescription("Nothing to do");
        jobParameters.setTimezone(DEFAULT_TIMEZONE);
        jobParameters.setCron(cron);
        jobParameters.setEnabled(true);
        jobParameters.setClusterGroupId(clusterGroup.getId());
        jobParameters.setScheduleGroupId(scheduleGroup.getId());
        jobParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);
        jobParameters.setActionIdentifier("action.type");
        jobParameters.setActionDefinition("action.data".getBytes());

        ScheduleJob scheduleJob = scheduleJobManagementService.createJob(jobParameters);
        Assert.assertNotNull(scheduleJob);

        ScheduleAction currentScheduleAction = scheduleJob.getAction();
        Assert.assertNotNull(currentScheduleAction);
        Assert.assertEquals("action.type", currentScheduleAction.getIdentifier());
        Assert.assertTrue(Arrays.equals("action.data".getBytes(), currentScheduleAction.getDefinition()));
        Assert.assertFalse(currentScheduleAction.isArchived());

        ScheduleJobActionParameters actionParameters = new ScheduleJobActionParameters();
        actionParameters.setIdentifier("action.type.2");
        actionParameters.setDefinition("action.data.2".getBytes());

        ScheduleJob scheduleJobModified = scheduleJobManagementService.changeJobAction(scheduleJob.getId(), actionParameters);
        Assert.assertNotNull(scheduleJobModified);

        ScheduleAction newScheduleAction = scheduleJobModified.getAction();
        Assert.assertNotNull(newScheduleAction);
        Assert.assertEquals("action.type.2", newScheduleAction.getIdentifier());
        Assert.assertTrue(Arrays.equals("action.data.2".getBytes(), newScheduleAction.getDefinition()));
        Assert.assertFalse(newScheduleAction.isArchived());
        Assert.assertTrue(currentScheduleAction.isArchived());

        control.verify();
    }

    @Test
    public void testChangeJobGroupTest() throws Exception {
        String cron = "0 0 0 * * ?";

        ScheduleGroup scheduleGroup1 = scheduleJobManagementService.createGroup("test group 1");
        Assert.assertNotNull(scheduleGroup1);

        ScheduleGroup scheduleGroup2 = scheduleJobManagementService.createGroup("test group 2");
        Assert.assertNotNull(scheduleGroup2);

        ClusterGroup clusterGroup = new ClusterGroup();
        clusterGroup.setName("Test cluster group");
        businessEntityDao.save(clusterGroup);

        ClusterNode clusterNode = new ClusterNode();
        clusterNode.setAddress("10.2.0.1");
        clusterNode.setDescription("test node");
        clusterNode.setEnabled(true);
        clusterNode.setGroup(clusterGroup);
        clusterGroup.getNodes().add(clusterNode);

        control.reset();

        quartzManagementService.createJob(
            EasyMock.anyLong(),
            EasyMock.eq(cron),
            EasyMock.eq(DEFAULT_TIMEZONE),
            EasyMock.eq(true)
        );

        control.replay();

        ScheduleJobCreateParameters jobParameters = new ScheduleJobCreateParameters();
        jobParameters.setName("Test Job");
        jobParameters.setDescription("Nothing to do");
        jobParameters.setTimezone(DEFAULT_TIMEZONE);
        jobParameters.setCron(cron);
        jobParameters.setEnabled(true);
        jobParameters.setClusterGroupId(clusterGroup.getId());
        jobParameters.setScheduleGroupId(scheduleGroup2.getId());
        jobParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);
        jobParameters.setActionIdentifier("action.type");
        jobParameters.setActionDefinition("action.data".getBytes());

        ScheduleJob scheduleJob = scheduleJobManagementService.createJob(jobParameters);
        Assert.assertNotNull(scheduleJob);

        ScheduleAction currentScheduleAction = scheduleJob.getAction();
        Assert.assertNotNull(currentScheduleAction);
        Assert.assertEquals("action.type", currentScheduleAction.getIdentifier());
        Assert.assertTrue(Arrays.equals("action.data".getBytes(), currentScheduleAction.getDefinition()));
        Assert.assertFalse(currentScheduleAction.isArchived());

        ScheduleJob scheduleJobModified = scheduleJobManagementService.changeJobGroup(scheduleJob.getId(), scheduleGroup2.getId());
        Assert.assertNotNull(scheduleJobModified);

        control.verify();

        entityManager.flush();
        entityManager.clear();

        scheduleJobModified = scheduleJobManagementService.findJob(scheduleJob.getId());
        Assert.assertNotNull(scheduleJobModified);
        Assert.assertEquals("test group 2", scheduleJobModified.getGroup().getName());

        ScheduleGroup scheduleGroupModified = scheduleJobManagementService.findGroup(scheduleGroup1.getId());
        Assert.assertNotNull(scheduleGroupModified);
        Assert.assertEquals(0, scheduleGroupModified.getJobs().size());
    }

    @Test
    public void testReschedulePassiveJob() throws Exception {
        String cron1 = "0 0 0 * * ?";
        String cron2 = "1 0 0 * * ?";

        ScheduleGroup scheduleGroup = scheduleJobManagementService.createGroup("test group");
        Assert.assertNotNull(scheduleGroup);

        ClusterGroup clusterGroup = new ClusterGroup();
        clusterGroup.setName("Test cluster group");
        businessEntityDao.save(clusterGroup);

        ClusterNode clusterNode = new ClusterNode();
        clusterNode.setAddress("10.2.0.1");
        clusterNode.setDescription("test node");
        clusterNode.setEnabled(true);
        clusterNode.setGroup(clusterGroup);
        clusterGroup.getNodes().add(clusterNode);

        Capture<Long> idCapture = new Capture<Long>(CaptureType.ALL);

        control.reset();

        quartzManagementService.createJob(
            EasyMock.anyLong(),
            EasyMock.eq(cron1),
            EasyMock.eq(DEFAULT_TIMEZONE),
            EasyMock.eq(true)
        );

        quartzManagementService.disableJob(
            EasyMock.capture(idCapture)
        );

        control.replay();

        ScheduleJobCreateParameters jobParameters = new ScheduleJobCreateParameters();
        jobParameters.setName("Test Job");
        jobParameters.setDescription("Nothing to do");
        jobParameters.setTimezone(DEFAULT_TIMEZONE);
        jobParameters.setCron(cron1);
        jobParameters.setEnabled(true);
        jobParameters.setClusterGroupId(clusterGroup.getId());
        jobParameters.setScheduleGroupId(scheduleGroup.getId());
        jobParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);
        jobParameters.setActionIdentifier("action.type");
        jobParameters.setActionDefinition("action.data".getBytes());

        ScheduleJob scheduleJob = scheduleJobManagementService.createJob(jobParameters);
        Assert.assertNotNull(scheduleJob);

        ScheduleJob scheduleJobModified;

        scheduleJobModified = scheduleJobManagementService.disableJob(scheduleJob.getId());
        Assert.assertNotNull(scheduleJobModified);
        Assert.assertFalse(scheduleJobModified.isEnabled());

        ScheduleJobRescheduleParameters rescheduleParameters = new ScheduleJobRescheduleParameters();
        rescheduleParameters.setCron(cron2);
        rescheduleParameters.setTimezone(DEFAULT_TIMEZONE);

        scheduleJobModified = scheduleJobManagementService.rescheduleJob(scheduleJob.getId(), rescheduleParameters);
        Assert.assertNotNull(scheduleJobModified);
        Assert.assertFalse(scheduleJobModified.isEnabled());

        control.verify();

        Assert.assertEquals(scheduleJobModified.getId(), idCapture.getValue());
    }

}
