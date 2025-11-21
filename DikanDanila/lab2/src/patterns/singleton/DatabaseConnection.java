package patterns.singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseConnection {
    private static volatile DatabaseConnection instance;
    private final Connection connection;

    private static final String JDBC_URL = "jdbc:h2:mem:bankdb;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private DatabaseConnection() {
        try {
            Class.forName("org.h2.Driver");
            this.connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            System.out.println("[SINGLETON] Соединение с базой данных успешно установлено.");

            initTables();

        } catch (ClassNotFoundException e) {
            System.err.println("Ошибка: JDBC драйвер для H2 не найден.");
            throw new RuntimeException("JDBC драйвер не найден", e);
        } catch (SQLException e) {
            System.err.println("Ошибка при установке соединения с базой данных.");
            throw new RuntimeException("Не удалось установить соединение с БД", e);
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void initTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS transactions");
            stmt.execute("DROP TABLE IF EXISTS accounts");
            stmt.execute("DROP TABLE IF EXISTS users");

            stmt.execute("CREATE TABLE users (" +
                    "uuid VARCHAR(36) PRIMARY KEY, " +
                    "nickname VARCHAR(255))");

            stmt.execute("CREATE TABLE accounts (" +
                    "id VARCHAR(36) PRIMARY KEY, " +
                    "user_uuid VARCHAR(36), " +
                    "balance BIGINT, " +
                    "isFrozen BOOLEAN)");

            stmt.execute("CREATE TABLE transactions (" +
                    "uuid VARCHAR(36) PRIMARY KEY, " +
                    "from_account_id VARCHAR(36), " +
                    "to_account_id VARCHAR(36), " +
                    "amount BIGINT, " +
                    "type VARCHAR(50), " +
                    "status VARCHAR(50), " +
                    "timestamp TIMESTAMP)");
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[SINGLETON] Соединение с базой данных закрыто.");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при закрытии соединения с БД.");
            e.printStackTrace();
        }
    }
}