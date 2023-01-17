package nro.main;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataSource {
    private static final String DB_URL = "jdbc:mysql://127.0.0.1/" + MainManager.mysql_database + "?autoReconnect=true&useSSL=false";
    //"?autoReconnect=true&useSSL=false"
    private static final String USER = MainManager.mysql_user;
    private static final String PASS = MainManager.mysql_pass;
    private static final HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;
    public static Connection connSaveData = null;

    public static int maximumPoolSize = 100;

    static {
        config.setJdbcUrl(DB_URL);
        config.setUsername(USER);
        config.setPassword(PASS);
        config.setAutoCommit(false);
        config.setMaximumPoolSize(maximumPoolSize);
        config.setConnectionTimeout(3000);
        config.addDataSourceProperty("cachePrepStmts", "true");
//        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSize", "50");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        ds = new HikariDataSource(config);

        try {
            connSaveData = ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private DataSource() { }

    public static Connection getConnection() throws SQLException {
//        System.out.println("SIZE POOL: " + ds.getMaximumPoolSize());
        countActiveConnection();
        return ds.getConnection();
    }

    public static Connection getConnSaveData() throws SQLException {
        if(connSaveData == null) {
            connSaveData = getConnection();
        }
        return connSaveData;
    }

    public static void countActiveConnection() {
        int countActive = ds.getHikariPoolMXBean().getActiveConnections();
//        if(countActive >= maximumPoolSize - 1) {
//            ds.close();
//
//            ds = new HikariDataSource(config);
//
//            try {
//                connSaveData = ds.getConnection();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//        Util.log("countConnectionActive: " + countActive);
    }

    public static int resultSize(@NotNull ResultSet rs){
        int count = 0;
        try {
            rs.last();
            count = rs.getRow();
            rs.beforeFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
}
