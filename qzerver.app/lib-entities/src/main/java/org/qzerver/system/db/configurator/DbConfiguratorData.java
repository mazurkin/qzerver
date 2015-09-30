package org.qzerver.system.db.configurator;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public final class DbConfiguratorData {

    public static final Map<DbConfiguratorType, String> HIBERNATE_DIALECTS =
        ImmutableMap.<DbConfiguratorType, String>builder()
            .put(DbConfiguratorType.HSQLDB, "org.hibernate.dialect.HSQLDialect")
            .put(DbConfiguratorType.MYSQL_INNO, "org.hibernate.dialect.MySQLInnoDBDialect")
            .put(DbConfiguratorType.POSTGRES, "org.hibernate.dialect.PostgreSQLDialect")
            .put(DbConfiguratorType.FIREBIRD, "org.hibernate.dialect.FirebirdDialect")
            .put(DbConfiguratorType.INTERBASE, "org.hibernate.dialect.InterbaseDialect")
            .put(DbConfiguratorType.ORACLE8I, "org.hibernate.dialect.Oracle8iDialect")
            .put(DbConfiguratorType.ORACLE9I, "org.hibernate.dialect.Oracle9iDialect")
            .put(DbConfiguratorType.ORACLE10G, "org.hibernate.dialect.Oracle10gDialect")
            .put(DbConfiguratorType.ORACLE11G, "org.hibernate.dialect.Oracle10gDialect")
            .put(DbConfiguratorType.MSSQL2005, "org.hibernate.dialect.SQLServer2005Dialect")
            .put(DbConfiguratorType.MSSQL2008, "org.hibernate.dialect.SQLServer2008Dialect")
            .put(DbConfiguratorType.DERBY_EMBEDDED, "org.hibernate.dialect.DerbyTenSevenDialect")
            .put(DbConfiguratorType.DERBY_CLIENT, "org.hibernate.dialect.DerbyTenSevenDialect")
            .build();

    public static final Map<DbConfiguratorType, String> QUARTZ_ADAPTERS =
        ImmutableMap.<DbConfiguratorType, String>builder()
            .put(DbConfiguratorType.HSQLDB, "org.quartz.impl.jdbcjobstore.HSQLDBDelegate")
            .put(DbConfiguratorType.MYSQL_INNO, "org.quartz.impl.jdbcjobstore.StdJDBCDelegate")
            .put(DbConfiguratorType.POSTGRES, "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate")
            .put(DbConfiguratorType.FIREBIRD, "org.quartz.impl.jdbcjobstore.StdJDBCDelegate")
            .put(DbConfiguratorType.INTERBASE, "org.quartz.impl.jdbcjobstore.StdJDBCDelegate")
            .put(DbConfiguratorType.ORACLE8I, "org.quartz.impl.jdbcjobstore.StdJDBCDelegate")
            .put(DbConfiguratorType.ORACLE9I, "org.quartz.impl.jdbcjobstore.StdJDBCDelegate")
            .put(DbConfiguratorType.ORACLE10G, "org.quartz.impl.jdbcjobstore.oracle.OracleDelegate")
            .put(DbConfiguratorType.ORACLE11G, "org.quartz.impl.jdbcjobstore.oracle.OracleDelegate")
            .put(DbConfiguratorType.MSSQL2005, "org.quartz.impl.jdbcjobstore.MSSQLDelegate")
            .put(DbConfiguratorType.MSSQL2008, "org.quartz.impl.jdbcjobstore.MSSQLDelegate")
            .put(DbConfiguratorType.DERBY_EMBEDDED, "org.quartz.impl.jdbcjobstore.CloudscapeDelegate")
            .put(DbConfiguratorType.DERBY_CLIENT, "org.quartz.impl.jdbcjobstore.CloudscapeDelegate")
            .build();

    public static final Map<DbConfiguratorType, String> JDBC_DRIVERS =
        ImmutableMap.<DbConfiguratorType, String>builder()
            .put(DbConfiguratorType.HSQLDB, "org.hsqldb.jdbc.JDBCDriver")
            .put(DbConfiguratorType.MYSQL_INNO, "com.mysql.jdbc.Driver")
            .put(DbConfiguratorType.POSTGRES, "org.postgresql.Driver")
            .put(DbConfiguratorType.FIREBIRD, "org.firebirdsql.jdbc.FBDriver")
            .put(DbConfiguratorType.INTERBASE, "interbase.interclient.Driver")
            .put(DbConfiguratorType.ORACLE8I, "oracle.jdbc.driver.OracleDriver")
            .put(DbConfiguratorType.ORACLE9I, "oracle.jdbc.OracleDriver")
            .put(DbConfiguratorType.ORACLE10G, "oracle.jdbc.OracleDriver")
            .put(DbConfiguratorType.ORACLE11G, "oracle.jdbc.OracleDriver")
            .put(DbConfiguratorType.MSSQL2005, "com.microsoft.sqlserver.jdbc.SQLServerDriver")
            .put(DbConfiguratorType.MSSQL2008, "com.microsoft.sqlserver.jdbc.SQLServerDriver")
            .put(DbConfiguratorType.DERBY_EMBEDDED, "org.apache.derby.jdbc.EmbeddedDriver")
            .put(DbConfiguratorType.DERBY_CLIENT, "org.apache.derby.jdbc.ClientDriver")
            .build();

    public static final Map<DbConfiguratorType, String> TEST_SQL_QUERIES =
        ImmutableMap.<DbConfiguratorType, String>builder()
            .put(DbConfiguratorType.HSQLDB, "SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS")
            .put(DbConfiguratorType.MYSQL_INNO, "SELECT 1")
            .put(DbConfiguratorType.POSTGRES, "SELECT 1")
            .put(DbConfiguratorType.FIREBIRD, "SELECT 1")
            .put(DbConfiguratorType.INTERBASE, "SELECT 1")
            .put(DbConfiguratorType.ORACLE8I, "SELECT 1 FROM DUAL")
            .put(DbConfiguratorType.ORACLE9I, "SELECT 1 FROM DUAL")
            .put(DbConfiguratorType.ORACLE10G, "SELECT 1 FROM DUAL")
            .put(DbConfiguratorType.ORACLE11G, "SELECT 1 FROM DUAL")
            .put(DbConfiguratorType.MSSQL2005, "SELECT 1")
            .put(DbConfiguratorType.MSSQL2008, "SELECT 1")
            .put(DbConfiguratorType.DERBY_EMBEDDED, "SELECT 1 FROM SYSIBM.SYSDUMMY1")
            .put(DbConfiguratorType.DERBY_CLIENT, "SELECT 1 FROM SYSIBM.SYSDUMMY1")
            .build();

    public static final Map<DbConfiguratorType, Boolean> HIBERNATE_NEW_GENERATORS =
            ImmutableMap.<DbConfiguratorType, Boolean>builder()
            .put(DbConfiguratorType.HSQLDB, true)
            .put(DbConfiguratorType.MYSQL_INNO, true)
            .put(DbConfiguratorType.POSTGRES, true)
            .put(DbConfiguratorType.FIREBIRD, false)
            .put(DbConfiguratorType.INTERBASE, false)
            .put(DbConfiguratorType.ORACLE8I, true)
            .put(DbConfiguratorType.ORACLE9I, true)
            .put(DbConfiguratorType.ORACLE10G, true)
            .put(DbConfiguratorType.ORACLE11G, true)
            .put(DbConfiguratorType.MSSQL2005, true)
            .put(DbConfiguratorType.MSSQL2008, true)
            .put(DbConfiguratorType.DERBY_EMBEDDED, true)
            .put(DbConfiguratorType.DERBY_CLIENT, true)
            .build();

    private DbConfiguratorData() {
    }

}
