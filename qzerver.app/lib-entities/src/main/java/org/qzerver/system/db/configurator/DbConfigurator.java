package org.qzerver.system.db.configurator;

import com.gainmatrix.lib.business.exception.SystemIntegrityException;
import com.google.common.base.Preconditions;

import javax.validation.constraints.NotNull;

import java.io.File;
import java.io.IOException;

public final class DbConfigurator {

    private static final String DERBY_LOG_PARAMETER = "derby.stream.error.file";

    @NotNull
    private DbConfiguratorType type = DbConfiguratorType.CUSTOM;

    /**
     * Hibernate dialect class
     * @return subclass of org.hibernate.dialect.Dialect
     */
    public String getHibernateDialect() {
        if (DbConfiguratorData.HIBERNATE_DIALECTS.containsKey(type)) {
            return DbConfiguratorData.HIBERNATE_DIALECTS.get(type);
        } else {
            throw new IllegalStateException("DB type is not supported: " + type);
        }
    }

    /**
     * Quartz adapter class
     * @return subclass of org.quartz.impl.jdbcjobstore.DriverDelegate
     */
    public String getQuartzAdapter() {
        if (DbConfiguratorData.QUARTZ_ADAPTERS.containsKey(type)) {
            return DbConfiguratorData.QUARTZ_ADAPTERS.get(type);
        } else {
            throw new IllegalStateException("DB type is not supported: " + type);
        }
    }

    /**
     * Database JDBC driver class
     * @return subclass of java.sql.Driver
     */
    public String getJdbcDriver() {
        if (DbConfiguratorData.JDBC_DRIVERS.containsKey(type)) {
            return DbConfiguratorData.JDBC_DRIVERS.get(type);
        } else {
            throw new IllegalStateException("DB type is not supported: " + type);
        }
    }

    /**
     * Get test SQL query
     * @return SQL query text
     */
    public String getTestSQLQuery() {
        if (DbConfiguratorData.TEST_SQL_QUERIES.containsKey(type)) {
            return DbConfiguratorData.TEST_SQL_QUERIES.get(type);
        } else {
            throw new IllegalStateException("DB type is not supported: " + type);
        }
    }

    /**
     * Type of generator
     * @return Is new generator type used
     */
    public boolean isNewGeneratorType() {
        if (DbConfiguratorData.HIBERNATE_NEW_GENERATORS.containsKey(type)) {
            return DbConfiguratorData.HIBERNATE_NEW_GENERATORS.get(type);
        } else {
            throw new IllegalStateException("DB type is not supported: " + type);
        }
    }

    /**
     * Init Derby-embedded internal properties
     */
    private void initDerbyEmbeddedSettings() {
        if (System.getProperty(DERBY_LOG_PARAMETER) == null) {
            File derbyLogFile;
            try {
                derbyLogFile = File.createTempFile("derby", ".log");
                derbyLogFile.deleteOnExit();
            } catch (IOException e) {
                throw new SystemIntegrityException("Failed to create a temporary file", e);
            }

            System.setProperty(DERBY_LOG_PARAMETER, derbyLogFile.getAbsolutePath());
        }
    }

    public void setType(DbConfiguratorType type) {
        Preconditions.checkNotNull(type, "DB type is not specified");

        this.type = type;

        switch (type) {
            case DERBY_EMBEDDED:
                initDerbyEmbeddedSettings();
                break;
            default:
                break;
        }
    }

}
