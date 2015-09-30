package org.qzerver.model.service.job.execution.impl;

import com.gainmatrix.lib.business.entity.BusinessEntityDao;
import com.gainmatrix.lib.paging.Extraction;
import com.gainmatrix.lib.time.ChronometerUtils;
import com.gainmatrix.lib.time.impl.StubChronometer;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.qzerver.base.AbstractTransactionalTest;
import org.qzerver.model.agent.action.providers.ActionResult;
import org.qzerver.model.dao.job.ScheduleExecutionDao;
import org.qzerver.model.domain.entities.cluster.ClusterGroup;
import org.qzerver.model.domain.entities.cluster.ClusterNode;
import org.qzerver.model.domain.entities.job.ScheduleExecution;
import org.qzerver.model.domain.entities.job.ScheduleExecutionNode;
import org.qzerver.model.domain.entities.job.ScheduleExecutionResult;
import org.qzerver.model.domain.entities.job.ScheduleExecutionStatus;
import org.qzerver.model.domain.entities.job.ScheduleExecutionStrategy;
import org.qzerver.model.domain.entities.job.ScheduleGroup;
import org.qzerver.model.domain.entities.job.ScheduleJob;
import org.qzerver.model.service.cluster.ClusterManagementService;
import org.qzerver.model.service.job.execution.dto.StartExecutionParameters;
import org.qzerver.model.service.job.management.ScheduleJobManagementService;
import org.qzerver.model.service.job.management.dto.ScheduleJobCreateParameters;
import org.qzerver.model.service.quartz.management.QuartzManagementService;
import org.springframework.validation.Validator;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

public class ScheduleExecutionManagementServiceImplTest extends AbstractTransactionalTest {

    private ScheduleExecutionManagementServiceImpl scheduleExecutionManagementService;

    private ScheduleGroup scheduleGroup;

    private ScheduleJob scheduleJob;

    private ClusterGroup clusterGroup;

    private ClusterNode clusterNode1;

    private ClusterNode clusterNode2;

    private ClusterNode clusterNode3;

    @Resource
    private QuartzManagementService quartzManagementService;

    @Resource
    private Validator modelBeanValidator;

    @Resource
    private StubChronometer chronometer;

    @Resource
    private ScheduleExecutionDao scheduleExecutionDao;

    @Resource
    private BusinessEntityDao businessEntityDao;

    @Resource
    private ScheduleJobManagementService scheduleJobManagementService;

    @Resource
    private ClusterManagementService clusterManagementService;

    @PersistenceContext
    private EntityManager entityManager;

    @Before
    public void setUp() throws Exception {
        scheduleExecutionManagementService = new ScheduleExecutionManagementServiceImpl();
        scheduleExecutionManagementService.setBeanValidator(modelBeanValidator);
        scheduleExecutionManagementService.setChronometer(chronometer);
        scheduleExecutionManagementService.setNode("test.example.com");
        scheduleExecutionManagementService.setScheduleExecutionDao(scheduleExecutionDao);
        scheduleExecutionManagementService.setClusterManagementService(clusterManagementService);
        scheduleExecutionManagementService.setBusinessEntityDao(businessEntityDao);

        scheduleGroup = scheduleJobManagementService.createGroup("Test group");

        clusterGroup = clusterManagementService.createGroup("Test group");
        clusterNode1 = clusterManagementService.createNode(clusterGroup.getId(), "10.2.0.1", "Node 1", true);
        clusterNode2 = clusterManagementService.createNode(clusterGroup.getId(), "10.2.0.2", "Node 2", true);
        clusterNode3 = clusterManagementService.createNode(clusterGroup.getId(), "10.2.0.3", "Node 3", true);

        ScheduleJobCreateParameters jobParameters = new ScheduleJobCreateParameters();
        jobParameters.setName("Test Job");
        jobParameters.setDescription("Nothing to do");
        jobParameters.setTimezone("UTC");
        jobParameters.setCron("0 0 0 * * ?");
        jobParameters.setEnabled(true);
        jobParameters.setClusterGroupId(clusterGroup.getId());
        jobParameters.setScheduleGroupId(scheduleGroup.getId());
        jobParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);
        jobParameters.setActionIdentifier("action.type");
        jobParameters.setActionDefinition("action.data".getBytes());

        scheduleJob = scheduleJobManagementService.createJob(jobParameters);

        // Disable Quartz job manually to prevent the firing from Quartz
        quartzManagementService.disableJob(scheduleJob.getId());
    }

    @Test
    public void testNormalExecution() throws Exception {
        // Start execution

        StartExecutionParameters startExecutionParameters = new StartExecutionParameters();
        startExecutionParameters.setManual(false);
        startExecutionParameters.setFired(ChronometerUtils.parseMoment("2012-01-02 12:32:12.000 UTC"));
        startExecutionParameters.setScheduled(ChronometerUtils.parseMoment("2012-01-02 12:32:12.000 UTC"));

        ScheduleExecution scheduleExecution =
            scheduleExecutionManagementService.startExecution(scheduleJob.getId(), startExecutionParameters);
        Assert.assertNotNull(scheduleExecution);
        Assert.assertNull(scheduleExecution.getFinished());

        entityManager.flush();
        entityManager.clear();

        ScheduleExecution scheduleExecutionModified =
            scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertNotNull(scheduleExecutionModified);
        Assert.assertEquals(scheduleExecution, scheduleExecutionModified);
        Assert.assertEquals(ScheduleExecutionStatus.INPROGRESS, scheduleExecutionModified.getStatus());
        Assert.assertEquals(3, scheduleExecutionModified.getNodes().size());

        ScheduleExecutionNode scheduleExecutionNode1 = scheduleExecutionModified.getNodes().get(0);
        Assert.assertNotNull(scheduleExecutionNode1);
        Assert.assertEquals(0, scheduleExecutionNode1.getOrderIndex());
        Assert.assertEquals(clusterNode2.getAddress(), scheduleExecutionNode1.getAddress());

        ScheduleExecutionNode scheduleExecutionNode2 = scheduleExecutionModified.getNodes().get(1);
        Assert.assertNotNull(scheduleExecutionNode2);
        Assert.assertEquals(1, scheduleExecutionNode2.getOrderIndex());
        Assert.assertEquals(clusterNode3.getAddress(), scheduleExecutionNode2.getAddress());

        ScheduleExecutionNode scheduleExecutionNode3 = scheduleExecutionModified.getNodes().get(2);
        Assert.assertNotNull(scheduleExecutionNode3);
        Assert.assertEquals(2, scheduleExecutionNode3.getOrderIndex());
        Assert.assertEquals(clusterNode1.getAddress(), scheduleExecutionNode3.getAddress());

        // Search all executions

        List<ScheduleExecution> scheduleExecutions = scheduleExecutionManagementService.findAll(Extraction.ALL);
        Assert.assertNotNull(scheduleExecutions);
        Assert.assertTrue(scheduleExecutions.size() > 0);
        Assert.assertTrue(Iterators.contains(scheduleExecutions.iterator(), scheduleExecution));

        // Make some progress

        ScheduleExecutionResult scheduleExecutionResult;

        scheduleExecutionResult = scheduleExecutionManagementService.startExecutionResult(scheduleExecutionNode1.getId());
        Assert.assertNotNull(scheduleExecutionResult);

        scheduleExecutionResult = scheduleExecutionManagementService.finishExecutionResult(scheduleExecutionResult.getId(),
            true, null);
        Assert.assertNotNull(scheduleExecutionResult);

        scheduleExecutionResult = scheduleExecutionManagementService.startExecutionResult(scheduleExecutionNode2.getId());
        Assert.assertNotNull(scheduleExecutionResult);

        scheduleExecutionResult = scheduleExecutionManagementService.finishExecutionResult(scheduleExecutionResult.getId(),
            true, null);
        Assert.assertNotNull(scheduleExecutionResult);

        scheduleExecutionResult = scheduleExecutionManagementService.startExecutionResult(scheduleExecutionNode3.getId());
        Assert.assertNotNull(scheduleExecutionResult);

        scheduleExecutionResult = scheduleExecutionManagementService.finishExecutionResult(scheduleExecutionResult.getId(),
            false, null);
        Assert.assertNotNull(scheduleExecutionResult);

        entityManager.flush();
        entityManager.clear();

        scheduleExecutionModified =
            scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertNotNull(scheduleExecutionModified);
        Assert.assertEquals(scheduleExecution, scheduleExecutionModified);
        Assert.assertEquals(3, scheduleExecutionModified.getResults().size());

        scheduleExecutionResult = scheduleExecutionModified.getResults().get(0);
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertEquals(0, scheduleExecutionResult.getOrderIndex());
        Assert.assertTrue(scheduleExecutionResult.isSucceed());

        scheduleExecutionResult = scheduleExecutionModified.getResults().get(1);
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertEquals(1, scheduleExecutionResult.getOrderIndex());
        Assert.assertTrue(scheduleExecutionResult.isSucceed());

        scheduleExecutionResult = scheduleExecutionModified.getResults().get(2);
        Assert.assertEquals(2, scheduleExecutionResult.getOrderIndex());
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertFalse(scheduleExecutionResult.isSucceed());

        // Finish execution

        scheduleExecutionModified = scheduleExecutionManagementService.finishExecution(
            scheduleExecution.getId(), ScheduleExecutionStatus.SUCCEED);
        Assert.assertNotNull(scheduleExecutionModified);
        Assert.assertNotNull(scheduleExecutionModified.getFinished());

        entityManager.flush();
        entityManager.clear();

        scheduleExecutionModified =
            scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertNotNull(scheduleExecutionModified);

        // Executions by job

        scheduleExecutions = scheduleExecutionManagementService.findByJob(scheduleJob.getId(), Extraction.ALL);
        Assert.assertNotNull(scheduleExecutions);
        Assert.assertEquals(1, scheduleExecutions.size());
        Assert.assertEquals(scheduleExecutionModified, scheduleExecutions.get(0));
    }

    @Test
    public void testCancelExecution() throws Exception {
        // Start execution

        StartExecutionParameters startExecutionParameters = new StartExecutionParameters();
        startExecutionParameters.setManual(false);
        startExecutionParameters.setFired(ChronometerUtils.parseMoment("2012-01-02 12:32:12.000 UTC"));
        startExecutionParameters.setScheduled(ChronometerUtils.parseMoment("2012-01-02 12:32:12.000 UTC"));

        ScheduleExecution scheduleExecution =
            scheduleExecutionManagementService.startExecution(scheduleJob.getId(), startExecutionParameters);
        Assert.assertNotNull(scheduleExecution);
        Assert.assertNull(scheduleExecution.getFinished());

        // Cancel execution

        ScheduleExecution scheduleExecutionModified = scheduleExecutionManagementService.cancelExecution(
            scheduleExecution.getId());
        Assert.assertNotNull(scheduleExecutionModified);
        Assert.assertNull(scheduleExecutionModified.getFinished());
        Assert.assertTrue(scheduleExecutionModified.isCancelled());

        entityManager.flush();
        entityManager.clear();

        scheduleExecutionModified = scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertNotNull(scheduleExecutionModified);
        Assert.assertNull(scheduleExecutionModified.getFinished());
        Assert.assertTrue(scheduleExecutionModified.isCancelled());

        // Finish execution

        scheduleExecutionModified = scheduleExecutionManagementService.finishExecution(
            scheduleExecution.getId(), ScheduleExecutionStatus.SUCCEED);
        Assert.assertNotNull(scheduleExecutionModified);
        Assert.assertNotNull(scheduleExecutionModified.getFinished());

        entityManager.flush();
        entityManager.clear();

        scheduleExecutionModified =
            scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertNotNull(scheduleExecutionModified);
        Assert.assertNotNull(scheduleExecutionModified.getFinished());
        Assert.assertFalse(scheduleExecutionModified.isCancelled());
    }

    @Test
    public void testSpecifiedCreation() throws Exception {
        StartExecutionParameters startExecutionParameters = new StartExecutionParameters();
        startExecutionParameters.setManual(false);
        startExecutionParameters.setFired(ChronometerUtils.parseMoment("2012-01-02 12:32:12.000 UTC"));
        startExecutionParameters.setScheduled(ChronometerUtils.parseMoment("2012-01-02 12:32:12.000 UTC"));
        startExecutionParameters.setComment("Test comment");
        startExecutionParameters.setAddresses(Lists.newArrayList("192.168.1.1", "192.168.1.2"));

        ScheduleExecution scheduleExecution =
            scheduleExecutionManagementService.startExecution(scheduleJob.getId(), startExecutionParameters);
        Assert.assertNotNull(scheduleExecution);
        Assert.assertNull(scheduleExecution.getFinished());

        entityManager.flush();
        entityManager.clear();

        ScheduleExecution scheduleExecutionModified =
            scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertNotNull(scheduleExecutionModified);
        Assert.assertEquals(scheduleExecution, scheduleExecutionModified);
        Assert.assertEquals("Test comment", scheduleExecutionModified.getDescription());
        Assert.assertEquals(ScheduleExecutionStatus.INPROGRESS, scheduleExecutionModified.getStatus());
        Assert.assertEquals(2, scheduleExecutionModified.getNodes().size());

        ScheduleExecutionNode scheduleExecutionNode;

        scheduleExecutionNode = scheduleExecutionModified.getNodes().get(0);
        Assert.assertEquals("192.168.1.1", scheduleExecutionNode.getAddress());

        scheduleExecutionNode = scheduleExecutionModified.getNodes().get(1);
        Assert.assertEquals("192.168.1.2", scheduleExecutionNode.getAddress());
    }

    @Test
    public void testLocalhostCreation() throws Exception {
        ScheduleJobCreateParameters jobParameters = new ScheduleJobCreateParameters();
        jobParameters.setName("Test Job");
        jobParameters.setDescription("Nothing to do");
        jobParameters.setTimezone("UTC");
        jobParameters.setCron("0 0 0 * * ?");
        jobParameters.setEnabled(true);
        jobParameters.setClusterGroupId(null);
        jobParameters.setScheduleGroupId(scheduleGroup.getId());
        jobParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);
        jobParameters.setActionIdentifier("action.type");
        jobParameters.setActionDefinition("action.data".getBytes());

        ScheduleJob localhostScheduleJob = scheduleJobManagementService.createJob(jobParameters);

        StartExecutionParameters startExecutionParameters = new StartExecutionParameters();
        startExecutionParameters.setManual(false);
        startExecutionParameters.setFired(ChronometerUtils.parseMoment("2012-01-02 12:32:12.000 UTC"));
        startExecutionParameters.setScheduled(ChronometerUtils.parseMoment("2012-01-02 12:32:12.000 UTC"));
        startExecutionParameters.setComment("Test comment");

        ScheduleExecution scheduleExecution =
            scheduleExecutionManagementService.startExecution(localhostScheduleJob.getId(), startExecutionParameters);
        Assert.assertNotNull(scheduleExecution);
        Assert.assertNull(scheduleExecution.getFinished());

        entityManager.flush();
        entityManager.clear();

        ScheduleExecution scheduleExecutionModified =
            scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertNotNull(scheduleExecutionModified);
        Assert.assertEquals(scheduleExecution, scheduleExecutionModified);
        Assert.assertEquals("Test comment", scheduleExecutionModified.getDescription());
        Assert.assertEquals(ScheduleExecutionStatus.INPROGRESS, scheduleExecutionModified.getStatus());
        Assert.assertEquals(1, scheduleExecutionModified.getNodes().size());

        ScheduleExecutionNode scheduleExecutionNode;

        scheduleExecutionNode = scheduleExecutionModified.getNodes().get(0);
        Assert.assertEquals("localhost", scheduleExecutionNode.getAddress());
    }

    @Test
    public void testFindFinished() throws Exception {
        StartExecutionParameters startExecutionParameters = new StartExecutionParameters();

        // Execution 1 - finished

        startExecutionParameters.setManual(false);
        startExecutionParameters.setFired(ChronometerUtils.parseMoment("2012-01-02 12:32:12.000 UTC"));
        startExecutionParameters.setScheduled(ChronometerUtils.parseMoment("2012-01-02 12:32:12.000 UTC"));
        startExecutionParameters.setComment("Test comment");

        ScheduleExecution scheduleExecution1 =
            scheduleExecutionManagementService.startExecution(scheduleJob.getId(), startExecutionParameters);

        scheduleExecutionManagementService.finishExecution(
            scheduleExecution1.getId(), ScheduleExecutionStatus.SUCCEED);

        // Execution 2 - non-finished

        startExecutionParameters.setManual(false);
        startExecutionParameters.setFired(ChronometerUtils.parseMoment("2012-01-02 12:32:13.000 UTC"));
        startExecutionParameters.setScheduled(ChronometerUtils.parseMoment("2012-01-02 12:33:12.000 UTC"));
        startExecutionParameters.setComment("Test comment");

        ScheduleExecution scheduleExecution2 =
            scheduleExecutionManagementService.startExecution(scheduleJob.getId(), startExecutionParameters);

        // Check

        List<ScheduleExecution> scheduleExecutions = scheduleExecutionManagementService.findFinished(Extraction.ALL);
        Assert.assertNotNull(scheduleExecutions);
        Assert.assertTrue(scheduleExecutions.size() > 0);
        Assert.assertTrue(Iterators.contains(scheduleExecutions.iterator(), scheduleExecution1));
        Assert.assertFalse(Iterators.contains(scheduleExecutions.iterator(), scheduleExecution2));
    }

    @Test
    public void testFindEngaged() throws Exception {
        StartExecutionParameters startExecutionParameters = new StartExecutionParameters();

        // Execution 1 - finished

        startExecutionParameters.setManual(false);
        startExecutionParameters.setFired(ChronometerUtils.parseMoment("2012-01-02 12:32:12.000 UTC"));
        startExecutionParameters.setScheduled(ChronometerUtils.parseMoment("2012-01-02 12:32:12.000 UTC"));
        startExecutionParameters.setComment("Test comment");

        ScheduleExecution scheduleExecution1 =
            scheduleExecutionManagementService.startExecution(scheduleJob.getId(), startExecutionParameters);

        scheduleExecutionManagementService.finishExecution(
            scheduleExecution1.getId(), ScheduleExecutionStatus.SUCCEED);

        // Execution 2 - non-finished

        startExecutionParameters.setManual(false);
        startExecutionParameters.setFired(ChronometerUtils.parseMoment("2012-01-02 12:32:13.000 UTC"));
        startExecutionParameters.setScheduled(ChronometerUtils.parseMoment("2012-01-02 12:33:12.000 UTC"));
        startExecutionParameters.setComment("Test comment");

        ScheduleExecution scheduleExecution2 =
            scheduleExecutionManagementService.startExecution(scheduleJob.getId(), startExecutionParameters);

        // Check

        List<ScheduleExecution> scheduleExecutions = scheduleExecutionManagementService.findEngaged(Extraction.ALL);
        Assert.assertNotNull(scheduleExecutions);
        Assert.assertTrue(scheduleExecutions.size() > 0);
        Assert.assertFalse(Iterators.contains(scheduleExecutions.iterator(), scheduleExecution1));
        Assert.assertTrue(Iterators.contains(scheduleExecutions.iterator(), scheduleExecution2));
    }

    private static class ActionResultStub implements ActionResult {

        private boolean succeed;

        private ActionResultStub(boolean succeed) {
            this.succeed = succeed;
        }

        @Override
        public boolean isSucceed() {
            return succeed;
        }

    }


}
