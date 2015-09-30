package org.qzerver.model.agent.mail;

import org.springframework.stereotype.Service;

/**
 * Abstract mail sender definition
 */
@Service
public interface MailAgent {

    /**
     * Send mail message
     * @param emailTo Target email address
     * @param subject Subject
     * @param text Message
     * @throws MailAgentException Exception on mail error
     */
    void sendMail(String emailTo, String subject, String text) throws MailAgentException;

}
