package org.qzerver.model.service.mail.impl;

import com.gainmatrix.lib.business.exception.SystemIntegrityException;
import com.gainmatrix.lib.template.TextTemplate;
import com.gainmatrix.lib.template.TextTemplateException;
import com.gainmatrix.lib.template.TextTemplateFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.hibernate.validator.constraints.Email;
import org.qzerver.model.agent.mail.MailAgent;
import org.qzerver.model.agent.mail.MailAgentException;
import org.qzerver.model.domain.entities.job.ScheduleExecution;
import org.qzerver.model.service.mail.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.scheduling.annotation.Async;

import javax.validation.constraints.NotNull;

import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class MailServiceImpl implements MailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailServiceImpl.class);

    private static final String DEFAULT_SUBJECT = "Qzerver notification";

    private static final String NAME_JOB_FAILED = "job-failed";

    @NotNull
    @Email
    private String mailTo;

    @NotNull
    private MailAgent mailAgent;

    @NotNull
    private TextTemplateFactory textTemplateFactory;

    @NotNull
    private boolean enabled;

    @NotNull
    private Locale locale;

    @NotNull
    private TimeZone timezone;

    @NotNull
    private MessageSourceAccessor messageSourceAccessor;

    @NotNull
    private String node;

    @Async
    @Override
    public void notifyJobExecutionFailed(ScheduleExecution execution) {
        Preconditions.checkNotNull(execution, "Execution is null");

        Map<String, Object> attributes = ImmutableMap.<String, Object>builder()
            .put("execution", execution)
            .build();

        Object[] subjectArguments = {
            execution.getName()
        };

        sendTemplatedMail(NAME_JOB_FAILED, attributes, subjectArguments);
    }

    protected void sendTemplatedMail(String name, Map<String, Object> attributes, Object[] subjectArguments) {
        Preconditions.checkNotNull(name, "Mail template name is null");
        Preconditions.checkNotNull(attributes, "Mail attribute container is null");

        // Check: is mailing required
        if (!enabled) {
            LOGGER.debug("Message [" + name + "] is not sent because mailing is off");
            return;
        }

        // Load template
        TextTemplate textTemplate;

        try {
            textTemplate = textTemplateFactory.getTemplate(name, locale);
        } catch (TextTemplateException e) {
            throw new SystemIntegrityException("Template [" + name + "] is not found", e);
        }

        // Prepare model
        Map<String, Object> model = ImmutableMap.<String, Object>builder()
            .put("locale", locale)
            .put("timezone", timezone)
            .putAll(attributes)
            .build();

        // Render template
        String text;

        try {
            text = textTemplate.render(model);
        } catch (TextTemplateException e) {
            throw new SystemIntegrityException("Template [" + name + "] could not be rendered", e);
        }

        // Load subject
        String defaultSubject = messageSourceAccessor.getMessage("mail.subject.default",
            null, DEFAULT_SUBJECT, locale);

        String specificSubject = messageSourceAccessor.getMessage("mail.subject." + name,
            subjectArguments, defaultSubject, locale);

        String effectiveSubject = String.format("%s [%s]", specificSubject, node);

        // Send mail
        try {
            mailAgent.sendMail(mailTo, effectiveSubject, text);
        } catch (MailAgentException e) {
            LOGGER.error("Fail to send message [" + name + "]", e);
            return;
        }

        LOGGER.debug("Message [" + name + "] is sent");
    }

    @Required
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Required
    public void setMailAgent(MailAgent mailAgent) {
        this.mailAgent = mailAgent;
    }

    @Required
    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }

    @Required
    public void setTextTemplateFactory(TextTemplateFactory textTemplateFactory) {
        this.textTemplateFactory = textTemplateFactory;
    }

    @Required
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Required
    public void setTimezone(TimeZone timezone) {
        this.timezone = timezone;
    }

    @Required
    public void setMessageSourceAccessor(MessageSourceAccessor messageSourceAccessor) {
        this.messageSourceAccessor = messageSourceAccessor;
    }

    @Required
    public void setNode(String node) {
        this.node = node;
    }
}
