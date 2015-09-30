package org.qzerver.model.agent.action.providers;

import org.qzerver.model.agent.action.providers.executor.clazz.ClassActionDefinition;
import org.qzerver.model.agent.action.providers.executor.clazz.ClassActionResult;
import org.qzerver.model.agent.action.providers.executor.datagram.DatagramActionDefinition;
import org.qzerver.model.agent.action.providers.executor.datagram.DatagramActionResult;
import org.qzerver.model.agent.action.providers.executor.groovy.GroovyActionDefinition;
import org.qzerver.model.agent.action.providers.executor.groovy.GroovyActionResult;
import org.qzerver.model.agent.action.providers.executor.http.HttpActionDefinition;
import org.qzerver.model.agent.action.providers.executor.http.HttpActionResult;
import org.qzerver.model.agent.action.providers.executor.jdbc.JdbcActionDefinition;
import org.qzerver.model.agent.action.providers.executor.jdbc.JdbcActionResult;
import org.qzerver.model.agent.action.providers.executor.jmx.JmxActionDefinition;
import org.qzerver.model.agent.action.providers.executor.jmx.JmxActionResult;
import org.qzerver.model.agent.action.providers.executor.localcommand.LocalCommandActionDefinition;
import org.qzerver.model.agent.action.providers.executor.localcommand.LocalCommandActionResult;
import org.qzerver.model.agent.action.providers.executor.socket.SocketActionDefinition;
import org.qzerver.model.agent.action.providers.executor.socket.SocketActionResult;
import org.qzerver.model.agent.action.providers.executor.sshcommand.SshCommandActionDefinition;
import org.qzerver.model.agent.action.providers.executor.sshcommand.SshCommandActionResult;

public enum ActionIdentifier {

    LOCAL_COMMAND(LocalCommandActionDefinition.class, LocalCommandActionResult.class),

    SSH_COMMAND(SshCommandActionDefinition.class, SshCommandActionResult.class),

    HTTP(HttpActionDefinition.class, HttpActionResult.class),

    SOCKET(SocketActionDefinition.class, SocketActionResult.class),

    DATAGRAM(DatagramActionDefinition.class, DatagramActionResult.class),

    JMX(JmxActionDefinition.class, JmxActionResult.class),

    CLASS(ClassActionDefinition.class, ClassActionResult.class),

    GROOVY(GroovyActionDefinition.class, GroovyActionResult.class),

    JDBC(JdbcActionDefinition.class, JdbcActionResult.class);

    private final Class<? extends ActionDefinition> actionDefinitionClass;

    private final Class<? extends ActionResult> actionResultClass;

    private ActionIdentifier(
        Class<? extends ActionDefinition> actionDefinitionClass,
        Class<? extends ActionResult> actionResultClass)
    {
        this.actionDefinitionClass = actionDefinitionClass;
        this.actionResultClass = actionResultClass;
    }

    public String getIdentifier() {
        return name();
    }

    public Class<? extends ActionDefinition> getActionDefinitionClass() {
        return actionDefinitionClass;
    }

    public Class<? extends ActionResult> getActionResultClass() {
        return actionResultClass;
    }

    public static ActionIdentifier findByIdentifier(String identifier) {
        for (ActionIdentifier actionIdentifier : ActionIdentifier.values()) {
            if (actionIdentifier.getIdentifier().equalsIgnoreCase(identifier)) {
                return actionIdentifier;
            }
        }

        throw new IllegalArgumentException("Can't find action identifier by specified itentifier: " + identifier);
    }

}
