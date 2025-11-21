package repository;

import models.Account;
import models.Transaction;
import models.TransactionType;
import patterns.singleton.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AccountRepository {

    private final Connection connection;

    public AccountRepository() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        initSchema();
    }

    private void initSchema() {
        try (Statement statement = connection.createStatement()) {
            String sqlAccounts = "CREATE TABLE IF NOT EXISTS ACCOUNTS (" +
                    "id VARCHAR(255) PRIMARY KEY, " +
                    "user_uuid VARCHAR(255), " +
                    "balance BIGINT NOT NULL, " +
                    "isFrozen BOOLEAN NOT NULL)";
            statement.execute(sqlAccounts);

            String sqlTransactions = "CREATE TABLE IF NOT EXISTS TRANSACTIONS (" +
                    "uuid VARCHAR(255) PRIMARY KEY, " +
                    "from_account_id VARCHAR(255), " +
                    "to_account_id VARCHAR(255), " +
                    "amount BIGINT, " +
                    "type VARCHAR(50), " +
                    "status VARCHAR(50), " +
                    "timestamp TIMESTAMP)";
            statement.execute(sqlTransactions);

            System.out.println("[REPOSITORY] Схема базы данных (счета и транзакции) инициализирована.");
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось инициализировать схему БД", e);
        }
    }


    public void save(Account account) {
        String sql = "MERGE INTO ACCOUNTS (id, user_uuid, balance, isFrozen) KEY(id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, account.getId());
            statement.setString(2, account.getUserUuid());
            statement.setLong(3, account.getBalance());
            statement.setBoolean(4, account.isFrozen());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось сохранить счет " + account.getId(), e);
        }
    }

    public void update(Account account) {
        save(account);
    }

    public Optional<Account> findById(String id) {
        String sql = "SELECT * FROM ACCOUNTS WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Account account = new Account(
                            rs.getString("id"),
                            rs.getString("user_uuid"),
                            rs.getLong("balance"),
                            rs.getBoolean("isFrozen")
                    );
                    return Optional.of(account);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске счета " + id, e);
        }
        return Optional.empty();
    }

    public void saveTransaction(Transaction tx) {
        String sql = "MERGE INTO TRANSACTIONS (uuid, from_account_id, to_account_id, amount, type, status, timestamp) KEY(uuid) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, tx.getId().toString());
            ps.setString(2, tx.getFromAccountId());
            ps.setString(3, tx.getToAccountId());
            ps.setLong(4, tx.getAmount());
            ps.setString(5, tx.getType().toString());
            ps.setString(6, tx.getStatus().toString());
            ps.setTimestamp(7, Timestamp.valueOf(tx.getTimestamp()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось сохранить транзакцию", e);
        }
    }

    public List<Transaction> findAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM TRANSACTIONS ORDER BY timestamp DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Transaction tx = new Transaction(
                        UUID.fromString(rs.getString("uuid")),
                        TransactionType.valueOf(rs.getString("type")),
                        rs.getLong("amount"),
                        rs.getString("from_account_id"),
                        rs.getString("to_account_id"),
                        rs.getTimestamp("timestamp").toLocalDateTime(),
                        Transaction.Status.valueOf(rs.getString("status"))
                );
                transactions.add(tx);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
}