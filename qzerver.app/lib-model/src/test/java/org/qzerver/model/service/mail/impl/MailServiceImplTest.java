package org.qzerver.model.service.mail.impl;

import com.gainmatrix.lib.template.TextTemplateFactory;
import com.gainmatrix.lib.time.ChronometerUtils;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;
import org.qzerver.base.AbstractModelTest;
import org.qzerver.model.agent.mail.MailAgent;
import org.qzerver.model.domain.entities.job.ScheduleExecution;
import org.qzerver.model.domain.entities.job.ScheduleExecutionStatus;
import org.springframework.context.support.MessageSourceAccessor;

import javax.annotation.Resource;

import java.util.Locale;
import java.util.TimeZone;

public class MailServiceImplTest extends AbstractModelTest {

    private IMocksControl control;

    private MailServiceImpl mailService;

    private MailAgent mailAgent;

    @Resource
    private MessageSourceAccessor messageSourceAccessor;

    @Resource
    private TextTemplateFactory mailFreemarkerTemplateFactory;

    @Before
    public void setUp() throws Exception {
        control = EasyMock.createStrictControl();

        mailAgent = control.createMock(MailAgent.class);

        mailService = new MailServiceImpl();
        mailService.setEnabled(true);
        mailService.setLocale(Locale.ENGLISH);
        mailService.setTimezone(TimeZone.getTimeZone("UTC"));
        mailService.setMailTo("nobody@example.com");
        mailService.setMailAgent(mailAgent);
        mailService.setMessageSourceAccessor(messageSourceAccessor);
        mailService.setTextTemplateFactory(mailFreemarkerTemplateFactory);
    }

    @Test
    public void testJobExecutionFailed() throws Exception {
        ScheduleExecution scheduleExecution = new ScheduleExecution();
        scheduleExecution.setName("aaaaa");
        scheduleExecution.setStatus(ScheduleExecutionStatus.EMPTYNODES);
        scheduleExecution.setFired(ChronometerUtils.parseMoment("2012-01-01 10:00:00.000 UTC"));
        scheduleExecution.setHostname("host1");
        scheduleExecution.setFinished(ChronometerUtils.parseMoment("2012-01-01 10:01:00.000 UTC"));

        control.reset();

        mailAgent.sendMail(
            EasyMock.<String>anyObject(),
            EasyMock.<String>anyObject(),
            EasyMock.<String>anyObject()
        );

        control.replay();

        mailService.notifyJobExecutionFailed(scheduleExecution);

        control.verify();
    }
}
