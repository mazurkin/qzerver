package org.qzerver.model.agent.action.providers.executor.jdbc;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.qzerver.base.AbstractModelTest;
import org.qzerver.model.agent.action.providers.ActionExecutor;

import javax.annotation.Resource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class JdbcActionExecutorTest extends AbstractModelTest {
    
    @Resource
    private ActionExecutor jdbcActionExecutor;

    private static final String JDBC_CLASS = "org.hsqldb.jdbcDriver";

    private static final String JDBC_URL = "jdbc:hsqldb:mem:jdbc_action_test";

    private static final String JDBC_USERNAME = "username";

    private static final String JDBC_PASSWORD = "password";

    @Before
    public void setUp() throws Exception {
        Class.forName(JDBC_CLASS);

        Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
        connection.setAutoCommit(true);
        connection.setReadOnly(false);
        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE TEST (V VARCHAR(64))");
        connection.close();
    }

    @After
    public void tearDown() throws Exception {
        Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
        connection.setAutoCommit(true);
        connection.setReadOnly(false);
        Statement statement = connection.createStatement();
        statement.execute("SHUTDOWN");
        connection.close();
    }

    @Test
    public void testNormal() throws Exception {
        JdbcActionDefinition definition = new JdbcActionDefinition();
        definition.setJdbcClass(JDBC_CLASS);
        definition.setJdbcUrl(JDBC_URL);
        definition.setUsername(JDBC_USERNAME);
        definition.setPassword(JDBC_PASSWORD);
        definition.setExpected(1);
        definition.setRelation(JdbcActionExpectedRelation.EQUAL);
        definition.setSql("INSERT INTO TEST (V) VALUES ('ABC')");

        JdbcActionResult result = (JdbcActionResult) jdbcActionExecutor.execute(definition, 123L, "doesn't matter");
        Assert.assertNotNull(result);
        Assert.assertEquals(true, result.isSucceed());
        Assert.assertEquals(1, result.getModified());
        Assert.assertNull(result.getExceptionClass());
        Assert.assertNull(result.getExceptionMessage());
    }

    @Test
    public void testException() throws Exception {
        JdbcActionDefinition definition = new JdbcActionDefinition();
        definition.setJdbcClass(JDBC_CLASS);
        definition.setJdbcUrl(JDBC_URL);
        definition.setUsername(JDBC_USERNAME);
        definition.setPassword(JDBC_PASSWORD);
        definition.setExpected(1);
        definition.setRelation(JdbcActionExpectedRelation.EQUAL);
        definition.setSql("INSERT INTO TEST (XYZ) VALUES ('ABC')");

        JdbcActionResult result = (JdbcActionResult) jdbcActionExecutor.execute(definition, 123L, "doesn't matter");
        Assert.assertNotNull(result);
        Assert.assertEquals(false, result.isSucceed());
        Assert.assertEquals(0, result.getModified());
        Assert.assertEquals("java.sql.SQLSyntaxErrorException", result.getExceptionClass());
        Assert.assertNotNull(result.getExceptionMessage());
    }
}
