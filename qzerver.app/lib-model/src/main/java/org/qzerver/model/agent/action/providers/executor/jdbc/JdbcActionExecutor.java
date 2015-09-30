package org.qzerver.model.agent.action.providers.executor.jdbc;

import com.gainmatrix.lib.spring.validation.BeanValidationUtils;
import com.google.common.base.Preconditions;
import org.qzerver.model.agent.action.providers.ActionDefinition;
import org.qzerver.model.agent.action.providers.ActionExecutor;
import org.qzerver.model.agent.action.providers.ActionPlaceholders;
import org.qzerver.model.agent.action.providers.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Validator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class JdbcActionExecutor implements ActionExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcActionExecutor.class);

    private Validator beanValidator;

    @Override
    public ActionResult execute(ActionDefinition actionDefinition, long scheduleExecutionId, String nodeAddress) {
        Preconditions.checkNotNull(actionDefinition, "Definition is null");
        Preconditions.checkNotNull(nodeAddress, "Node is not specified");

        BeanValidationUtils.checkValidity(actionDefinition, beanValidator);

        JdbcActionDefinition definition = (JdbcActionDefinition) actionDefinition;

        LOGGER.debug("Execute jdbc query on node [{}]", nodeAddress);

        String effectiveUrl = definition.getJdbcUrl();
        effectiveUrl = ActionPlaceholders.substituteNode(effectiveUrl, nodeAddress);

        try {
            return processAction(definition, effectiveUrl);
        } catch (Exception e) {
            LOGGER.debug("Fail to execute jdbc query", e);
            return produceExceptionalResult(e);
        }
    }

    private JdbcActionResult produceExceptionalResult(Exception e) {
        JdbcActionResult result = new JdbcActionResult();

        result.setSucceed(false);
        result.setExceptionClass(e.getClass().getCanonicalName());
        result.setExceptionMessage(e.getLocalizedMessage());
        result.setModified(0);

        return result;
    }

    private JdbcActionResult processAction(JdbcActionDefinition definition, String effectiveUrl) throws Exception {
        Class.forName(definition.getJdbcClass());

        Connection connection = DriverManager.getConnection(effectiveUrl,
            definition.getUsername(), definition.getPassword());
        connection.setAutoCommit(false);
        connection.setReadOnly(false);

        try {
            JdbcActionResult result = processConnection(definition, connection);
            connection.commit();
            return result;
        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            connection.close();
        }
    }

    private JdbcActionResult processConnection(JdbcActionDefinition definition, Connection connection)
        throws Exception
    {
        Statement statement = connection.createStatement();

        try {
            return processStatement(definition, statement);
        } finally {
            statement.close();
        }

    }

    private JdbcActionResult processStatement(JdbcActionDefinition definition, Statement statement)
        throws Exception
    {
        if (definition.getTimeoutSec() > 0) {
            statement.setQueryTimeout(definition.getTimeoutSec());
        }

        int modified = statement.executeUpdate(definition.getSql());

        return processResult(definition, modified);
    }

    private JdbcActionResult processResult(JdbcActionDefinition definition, int modified)
        throws Exception
    {
        boolean succeed;

        switch (definition.getRelation()) {
            case ANY:
                succeed = true;
                break;
            case EQUAL:
                succeed = modified == definition.getExpected();
                break;
            case NOT_EQUAL:
                succeed = modified != definition.getExpected();
                break;
            case LESS_THAN:
                succeed = modified < definition.getExpected();
                break;
            case NOT_LESS_THAN:
                succeed = modified >= definition.getExpected();
                break;
            case MORE_THAN:
                succeed = modified > definition.getExpected();
                break;
            case NOT_MORE_THAN:
                succeed = modified <= definition.getExpected();
                break;
            default:
                succeed = false;
                break;
        }

        JdbcActionResult result = new JdbcActionResult();
        result.setModified(modified);
        result.setSucceed(succeed);
        return result;
    }

    @Required
    public void setBeanValidator(Validator beanValidator) {
        this.beanValidator = beanValidator;
    }
}
