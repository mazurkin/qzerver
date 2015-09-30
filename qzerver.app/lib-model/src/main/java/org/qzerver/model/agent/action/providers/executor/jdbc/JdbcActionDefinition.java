package org.qzerver.model.agent.action.providers.executor.jdbc;

import com.gainmatrix.lib.serialization.SerialVersionUID;
import org.hibernate.validator.constraints.NotBlank;
import org.qzerver.model.agent.action.providers.ActionDefinition;
import org.qzerver.model.agent.action.providers.ActionIdentifier;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import java.io.Serializable;

public class JdbcActionDefinition implements ActionDefinition, Serializable {

    private static final long serialVersionUID = SerialVersionUID.UNCONTROLLED;

    @NotNull
    @NotBlank
    private String jdbcClass;

    @NotNull
    @NotBlank
    private String jdbcUrl;

    @NotNull
    @NotBlank
    private String sql;

    private String username;

    private String password;

    @Min(0)
    private int expected;

    @Min(0)
    private int timeoutSec;

    private JdbcActionExpectedRelation relation;

    public String getJdbcClass() {
        return jdbcClass;
    }

    public void setJdbcClass(String jdbcClass) {
        this.jdbcClass = jdbcClass;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getExpected() {
        return expected;
    }

    public void setExpected(int expected) {
        this.expected = expected;
    }

    public JdbcActionExpectedRelation getRelation() {
        return relation;
    }

    public void setRelation(JdbcActionExpectedRelation relation) {
        this.relation = relation;
    }

    public int getTimeoutSec() {
        return timeoutSec;
    }

    public void setTimeoutSec(int timeoutSec) {
        this.timeoutSec = timeoutSec;
    }

    @Override
    public ActionIdentifier getIdentifier() {
        return ActionIdentifier.JDBC;
    }

}
