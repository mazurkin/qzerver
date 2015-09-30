package org.qzerver.model.service.job.executor.impl;

import com.gainmatrix.lib.time.ChronometerUtils;
import com.gainmatrix.lib.time.impl.StubChronometer;
import junit.framework.Assert;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;
import org.qzerver.base.AbstractModelTest;
import org.qzerver.model.agent.action.ActionAgent;
import org.qzerver.model.agent.action.ActionAgentResult;
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
import org.qzerver.model.service.job.execution.ScheduleExecutionManagementService;
import org.qzerver.model.service.job.executor.dto.AutomaticJobExecutionParameters;
import org.qzerver.model.service.job.management.ScheduleJobManagementService;
import org.qzerver.model.service.job.management.dto.ScheduleJobActionParameters;
import org.qzerver.model.service.job.management.dto.ScheduleJobCreateParameters;
import org.qzerver.model.service.mail.MailService;
import org.qzerver.model.service.quartz.management.QuartzManagementService;
import org.springframework.validation.Validator;

import javax.annotation.Resource;

public class ScheduleJobExecutorServiceAllParallelTest extends AbstractModelTest {

    private IMocksControl control;

    private MailService mailService;

    private ScheduleJobExecutorServiceImpl scheduleJobExecutorService;

    private ActionAgent actionAgent;

    private ScheduleGroup scheduleGroup;

    private ScheduleJob scheduleJob;

    private ClusterGroup clusterGroup;

    private ClusterNode clusterNode1;

    private ClusterNode clusterNode2;

    private ClusterNode clusterNode3;

    @Resource
    private ScheduleJobManagementService scheduleJobManagementService;

    @Resource
    private ClusterManagementService clusterManagementService;

    @Resource
    private QuartzManagementService quartzManagementService;

    @Resource
    private ScheduleExecutionManagementService scheduleExecutionManagementService;

    @Resource
    private Validator modelBeanValidator;

    @Resource
    private StubChronometer chronometer;

    @Before
    public void setUp() throws Exception {
        control = EasyMock.createControl();
        control.makeThreadSafe(true);

        mailService = control.createMock(MailService.class);
        actionAgent = control.createMock(ActionAgent.class);

        scheduleJobExecutorService = new ScheduleJobExecutorServiceImpl();
        scheduleJobExecutorService.setBeanValidator(modelBeanValidator);
        scheduleJobExecutorService.setChronometer(chronometer);
        scheduleJobExecutorService.setMailService(mailService);
        scheduleJobExecutorService.setExecutionManagementService(scheduleExecutionManagementService);
        scheduleJobExecutorService.setActionAgent(actionAgent);

        scheduleGroup = scheduleJobManagementService.createGroup("Test group");

        clusterGroup = clusterManagementService.createGroup("Test group");
        clusterNode1 = clusterManagementService.createNode(clusterGroup.getId(), "10.2.0.1", "Node 1", true);
        clusterNode2 = clusterManagementService.createNode(clusterGroup.getId(), "10.2.0.2", "Node 2", true);
        clusterNode3 = clusterManagementService.createNode(clusterGroup.getId(), "10.2.0.3", "Node 3", false);
    }

    @Test
    public void testNormalAutomaticAllExecution() throws Exception {
        ScheduleJobCreateParameters jobParameters = new ScheduleJobCreateParameters();
        jobParameters.setName("Test Job");
        jobParameters.setDescription("Nothing to do");
        jobParameters.setTimezone("UTC");
        jobParameters.setCron("0 0 0 * * ?");
        jobParameters.setEnabled(true);
        jobParameters.setAllNodes(true);
        jobParameters.setAllNodesPool(2);
        jobParameters.setClusterGroupId(clusterGroup.getId());
        jobParameters.setScheduleGroupId(scheduleGroup.getId());
        jobParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);
        jobParameters.setNotifyOnFailure(true);
        jobParameters.setActionIdentifier("action.type");
        jobParameters.setActionDefinition("action.data".getBytes());

        scheduleJob = scheduleJobManagementService.createJob(jobParameters);
        quartzManagementService.disableJob(scheduleJob.getId());

        control.reset();

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.anyLong(),
            EasyMock.<String>anyObject(),
            EasyMock.<byte[]>anyObject(),
            EasyMock.eq(clusterNode2.getAddress())
        )).andReturn(new ActionAgentResult(true, null));

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.anyLong(),
            EasyMock.<String>anyObject(),
            EasyMock.<byte[]>anyObject(),
            EasyMock.eq(clusterNode1.getAddress())
        )).andReturn(new ActionAgentResult(true, null));

        control.replay();

        AutomaticJobExecutionParameters jobExecutionParameters = new AutomaticJobExecutionParameters();
        jobExecutionParameters.setScheduledTime(ChronometerUtils.parseMoment("2012-02-20 10:00:00.000 UTC"));
        jobExecutionParameters.setFiredTime(ChronometerUtils.parseMoment("2012-02-20 10:00:00.231 UTC"));
        jobExecutionParameters.setNextFireTime(null);

        ScheduleExecution scheduleExecution = scheduleJobExecutorService.executeAutomaticJob(scheduleJob.getId(),
            jobExecutionParameters);
        Assert.assertNotNull(scheduleExecution);

        control.verify();

        ScheduleExecutionNode scheduleExecutionNode;
        ScheduleExecutionResult scheduleExecutionResult;

        scheduleExecution = scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertEquals(ScheduleExecutionStatus.SUCCEED, scheduleExecution.getStatus());
        Assert.assertEquals(2, scheduleExecution.getNodes().size());
        Assert.assertEquals(2, scheduleExecution.getResults().size());

        scheduleExecutionNode =scheduleExecution.getNodes().get(0);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode2.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertTrue(scheduleExecutionResult.isSucceed());

        scheduleExecutionNode =scheduleExecution.getNodes().get(1);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode1.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertTrue(scheduleExecutionResult.isSucceed());
    }

    @Test
    public void testLimitedAutomaticAllExecution() throws Exception {
        ScheduleJobCreateParameters jobParameters = new ScheduleJobCreateParameters();
        jobParameters.setName("Test Job");
        jobParameters.setDescription("Nothing to do");
        jobParameters.setTimezone("UTC");
        jobParameters.setCron("0 0 0 * * ?");
        jobParameters.setEnabled(true);
        jobParameters.setAllNodes(true);
        jobParameters.setAllNodesPool(2);
        jobParameters.setNodesLimit(1);
        jobParameters.setClusterGroupId(clusterGroup.getId());
        jobParameters.setScheduleGroupId(scheduleGroup.getId());
        jobParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);
        jobParameters.setNotifyOnFailure(true);
        jobParameters.setActionIdentifier("action.type");
        jobParameters.setActionDefinition("action.data".getBytes());

        scheduleJob = scheduleJobManagementService.createJob(jobParameters);
        quartzManagementService.disableJob(scheduleJob.getId());

        control.reset();

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.anyLong(),
            EasyMock.<String>anyObject(),
            EasyMock.<byte[]>anyObject(),
            EasyMock.eq(clusterNode2.getAddress())
        )).andReturn(new ActionAgentResult(true, null));

        control.replay();

        AutomaticJobExecutionParameters jobExecutionParameters = new AutomaticJobExecutionParameters();
        jobExecutionParameters.setScheduledTime(ChronometerUtils.parseMoment("2012-02-20 10:00:00.000 UTC"));
        jobExecutionParameters.setFiredTime(ChronometerUtils.parseMoment("2012-02-20 10:00:00.231 UTC"));
        jobExecutionParameters.setNextFireTime(null);

        ScheduleExecution scheduleExecution = scheduleJobExecutorService.executeAutomaticJob(scheduleJob.getId(),
            jobExecutionParameters);
        Assert.assertNotNull(scheduleExecution);

        control.verify();

        ScheduleExecutionNode scheduleExecutionNode;
        ScheduleExecutionResult scheduleExecutionResult;

        scheduleExecution = scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertEquals(ScheduleExecutionStatus.SUCCEED, scheduleExecution.getStatus());
        Assert.assertEquals(1, scheduleExecution.getNodes().size());
        Assert.assertEquals(1, scheduleExecution.getResults().size());

        scheduleExecutionNode =scheduleExecution.getNodes().get(0);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode2.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertTrue(scheduleExecutionResult.isSucceed());
    }

    @Test
    public void testOneFailedAutomaticAllExecution() throws Exception {
        ScheduleJobCreateParameters jobParameters = new ScheduleJobCreateParameters();
        jobParameters.setName("Test Job");
        jobParameters.setDescription("Nothing to do");
        jobParameters.setTimezone("UTC");
        jobParameters.setCron("0 0 0 * * ?");
        jobParameters.setEnabled(true);
        jobParameters.setAllNodes(true);
        jobParameters.setAllNodesPool(2);
        jobParameters.setClusterGroupId(clusterGroup.getId());
        jobParameters.setScheduleGroupId(scheduleGroup.getId());
        jobParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);
        jobParameters.setNotifyOnFailure(true);
        jobParameters.setActionIdentifier("action.type");
        jobParameters.setActionDefinition("action.data".getBytes());

        scheduleJob = scheduleJobManagementService.createJob(jobParameters);
        quartzManagementService.disableJob(scheduleJob.getId());

        control.reset();

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.anyLong(),
            EasyMock.<String>anyObject(),
            EasyMock.<byte[]>anyObject(),
            EasyMock.eq(clusterNode2.getAddress())
        )).andReturn(new ActionAgentResult(false, null));

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.anyLong(),
            EasyMock.<String>anyObject(),
            EasyMock.<byte[]>anyObject(),
            EasyMock.eq(clusterNode1.getAddress())
        )).andReturn(new ActionAgentResult(true, null));

        mailService.notifyJobExecutionFailed(
            EasyMock.<ScheduleExecution>anyObject()
        );

        control.replay();

        AutomaticJobExecutionParameters jobExecutionParameters = new AutomaticJobExecutionParameters();
        jobExecutionParameters.setScheduledTime(ChronometerUtils.parseMoment("2012-02-20 10:00:00.000 UTC"));
        jobExecutionParameters.setFiredTime(ChronometerUtils.parseMoment("2012-02-20 10:00:00.231 UTC"));
        jobExecutionParameters.setNextFireTime(null);

        ScheduleExecution scheduleExecution = scheduleJobExecutorService.executeAutomaticJob(scheduleJob.getId(),
            jobExecutionParameters);
        Assert.assertNotNull(scheduleExecution);

        control.verify();

        ScheduleExecutionNode scheduleExecutionNode;
        ScheduleExecutionResult scheduleExecutionResult;

        scheduleExecution = scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertEquals(ScheduleExecutionStatus.FAILED, scheduleExecution.getStatus());
        Assert.assertEquals(2, scheduleExecution.getNodes().size());
        Assert.assertEquals(2, scheduleExecution.getResults().size());

        scheduleExecutionNode =scheduleExecution.getNodes().get(0);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode2.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertFalse(scheduleExecutionResult.isSucceed());

        scheduleExecutionNode =scheduleExecution.getNodes().get(1);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode1.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertTrue(scheduleExecutionResult.isSucceed());
    }

    @Test
    public void testAllFailedAutomaticAllExecution() throws Exception {
        ScheduleJobCreateParameters jobParameters = new ScheduleJobCreateParameters();
        jobParameters.setName("Test Job");
        jobParameters.setDescription("Nothing to do");
        jobParameters.setTimezone("UTC");
        jobParameters.setCron("0 0 0 * * ?");
        jobParameters.setEnabled(true);
        jobParameters.setAllNodes(true);
        jobParameters.setAllNodesPool(2);
        jobParameters.setClusterGroupId(clusterGroup.getId());
        jobParameters.setScheduleGroupId(scheduleGroup.getId());
        jobParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);
        jobParameters.setNotifyOnFailure(true);
        jobParameters.setActionIdentifier("action.type");
        jobParameters.setActionDefinition("action.data".getBytes());

        scheduleJob = scheduleJobManagementService.createJob(jobParameters);
        quartzManagementService.disableJob(scheduleJob.getId());

        control.reset();

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.anyLong(),
            EasyMock.<String>anyObject(),
            EasyMock.<byte[]>anyObject(),
            EasyMock.eq(clusterNode2.getAddress())
        )).andReturn(new ActionAgentResult(false, null));

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.anyLong(),
            EasyMock.<String>anyObject(),
            EasyMock.<byte[]>anyObject(),
            EasyMock.eq(clusterNode1.getAddress())
        )).andReturn(new ActionAgentResult(false, null));

        mailService.notifyJobExecutionFailed(
            EasyMock.<ScheduleExecution>anyObject()
        );

        control.replay();

        AutomaticJobExecutionParameters jobExecutionParameters = new AutomaticJobExecutionParameters();
        jobExecutionParameters.setScheduledTime(ChronometerUtils.parseMoment("2012-02-20 10:00:00.000 UTC"));
        jobExecutionParameters.setFiredTime(ChronometerUtils.parseMoment("2012-02-20 10:00:00.231 UTC"));
        jobExecutionParameters.setNextFireTime(null);

        ScheduleExecution scheduleExecution = scheduleJobExecutorService.executeAutomaticJob(scheduleJob.getId(),
            jobExecutionParameters);
        Assert.assertNotNull(scheduleExecution);

        control.verify();

        ScheduleExecutionNode scheduleExecutionNode;
        ScheduleExecutionResult scheduleExecutionResult;

        scheduleExecution = scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertEquals(ScheduleExecutionStatus.FAILED, scheduleExecution.getStatus());
        Assert.assertEquals(2, scheduleExecution.getNodes().size());
        Assert.assertEquals(2, scheduleExecution.getResults().size());

        scheduleExecutionNode =scheduleExecution.getNodes().get(0);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode2.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertFalse(scheduleExecutionResult.isSucceed());

        scheduleExecutionNode =scheduleExecution.getNodes().get(1);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode1.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertFalse(scheduleExecutionResult.isSucceed());
    }

    @Test
    public void testExceptionAutomaticAllExecution() throws Exception {
        ScheduleJobActionParameters actionParameters = new ScheduleJobActionParameters();
        actionParameters.setIdentifier("action.type");
        actionParameters.setDefinition("action.data".getBytes());

        ScheduleJobCreateParameters jobParameters = new ScheduleJobCreateParameters();
        jobParameters.setName("Test Job");
        jobParameters.setDescription("Nothing to do");
        jobParameters.setTimezone("UTC");
        jobParameters.setCron("0 0 0 * * ?");
        jobParameters.setEnabled(true);
        jobParameters.setAllNodes(true);
        jobParameters.setAllNodesPool(2);
        jobParameters.setClusterGroupId(clusterGroup.getId());
        jobParameters.setScheduleGroupId(scheduleGroup.getId());
        jobParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);
        jobParameters.setNotifyOnFailure(true);
        jobParameters.setActionIdentifier("action.type");
        jobParameters.setActionDefinition("action.data".getBytes());

        scheduleJob = scheduleJobManagementService.createJob(jobParameters);
        quartzManagementService.disableJob(scheduleJob.getId());

        control.reset();

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.anyLong(),
            EasyMock.<String>anyObject(),
            EasyMock.<byte[]>anyObject(),
            EasyMock.eq(clusterNode2.getAddress())
        )).andThrow(new IllegalStateException("Test exception"));

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.anyLong(),
            EasyMock.<String>anyObject(),
            EasyMock.<byte[]>anyObject(),
            EasyMock.eq(clusterNode1.getAddress())
        )).andReturn(new ActionAgentResult(true, null));

        mailService.notifyJobExecutionFailed(
            EasyMock.<ScheduleExecution>anyObject()
        );

        control.replay();

        AutomaticJobExecutionParameters jobExecutionParameters = new AutomaticJobExecutionParameters();
        jobExecutionParameters.setScheduledTime(ChronometerUtils.parseMoment("2012-02-20 10:00:00.000 UTC"));
        jobExecutionParameters.setFiredTime(ChronometerUtils.parseMoment("2012-02-20 10:00:00.231 UTC"));
        jobExecutionParameters.setNextFireTime(null);

        ScheduleExecution scheduleExecution = scheduleJobExecutorService.executeAutomaticJob(scheduleJob.getId(),
            jobExecutionParameters);
        Assert.assertNotNull(scheduleExecution);

        control.verify();

        ScheduleExecutionNode scheduleExecutionNode;
        ScheduleExecutionResult scheduleExecutionResult;

        scheduleExecution = scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertEquals(ScheduleExecutionStatus.EXCEPTION, scheduleExecution.getStatus());
        Assert.assertEquals(2, scheduleExecution.getNodes().size());
        Assert.assertEquals(2, scheduleExecution.getResults().size());

        scheduleExecutionNode =scheduleExecution.getNodes().get(0);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode2.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertFalse(scheduleExecutionResult.isSucceed());

        scheduleExecutionNode =scheduleExecution.getNodes().get(1);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode1.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertTrue(scheduleExecutionResult.isSucceed());
    }

}
