package org.qzerver.model.agent.mail;

public class MailAgentException extends Exception {

    public MailAgentException() {
    }

    public MailAgentException(Throwable cause) {
        super(cause);
    }

    public MailAgentException(String message) {
        super(message);
    }

    public MailAgentException(String message, Throwable cause) {
        super(message, cause);
    }

}
