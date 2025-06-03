package helltech.dynamodb.persistence.rdbms;

import static java.util.Objects.nonNull;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;

public class RdbmsDataSource {

    private HikariDataSource dataSource;

    public RdbmsDataSource() {
        init();
    }

    public void init() {
        setupDataSource();
        createTables();
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    private void setupDataSource() {
        var config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        config.setUsername("sa");
        config.setPassword("");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        this.dataSource = new HikariDataSource(config);
    }

    private void createTables() {
        try (var connection = dataSource.getConnection(); var statement = connection.createStatement()) {
            statement.execute("""
                        CREATE TABLE "INSTITUTION" (id VARCHAR(60) PRIMARY KEY);""");
            statement.execute("""
                         CREATE TABLE "USER" (\
                             id VARCHAR(60) PRIMARY KEY, \
                             institution_id VARCHAR(60), \
                             FOREIGN KEY (institution_id) REFERENCES INSTITUTION(id));\
                         """);
            statement.execute("""
                                  CREATE TABLE "PUBLICATION" (\
                                      id VARCHAR(60) PRIMARY KEY,\
                                      user_id VARCHAR(60),\
                                      institution_id VARCHAR(60),\
                                      FOREIGN KEY (user_id) REFERENCES "USER"(id),\
                                      FOREIGN KEY (institution_id) REFERENCES "INSTITUTION"(id));\
                                  """);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void flush() {
        try (var connection = dataSource.getConnection();
            var statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM \"PUBLICATION\"");
            statement.executeUpdate("DELETE FROM \"USER\"");
            statement.executeUpdate("DELETE FROM \"INSTITUTION\"");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeDataSource() {
        if (nonNull(dataSource)) {
            dataSource.close();
        }
    }
}
