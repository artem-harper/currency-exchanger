package util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionManager {

    private static final String JDBCDriver = "org.sqlite.JDBC";
    private static final String URL = "jdbc:sqlite::resource:currencies.sqlite";
    private static final HikariDataSource dataSource = new HikariDataSource();

    static{
        dataSource.setDriverClassName(JDBCDriver);
        dataSource.setJdbcUrl(URL);

    }

    public static Connection get() throws SQLException {
        return dataSource.getConnection();
    }
}
