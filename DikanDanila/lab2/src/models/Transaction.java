package models;

import models.TransactionType;

import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    public enum Status {
        PENDING,
        COMPLETED,
        FAILED
    }

    private final UUID id;
    private final LocalDateTime timestamp;
    private final TransactionType type;
    private final long amount;
    private final String fromAccountId;
    private final String toAccountId;

    private volatile Status status;

    public Transaction(TransactionType type, long amount, String fromAccountId) {
        this(type, amount, fromAccountId, null);
    }

    public Transaction(TransactionType type, long amount, String fromAccountId, String toAccountId) {
        if (type == null || fromAccountId == null) {
            throw new IllegalArgumentException("Тип транзакции и ID счета-источника не могут быть null.");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Сумма транзакции не может быть отрицательной.");
        }
        if (type == TransactionType.TRANSFER && toAccountId == null) {
            throw new IllegalArgumentException("Для перевода необходимо указать счет-получатель.");
        }
        if (type != TransactionType.TRANSFER && toAccountId != null) {
            throw new IllegalArgumentException("Счет-получатель указывается только для переводов.");
        }

        this.id = UUID.randomUUID();
        this.timestamp = LocalDateTime.now();
        this.type = type;
        this.amount = amount;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.status = Status.PENDING;
    }

    // Нужен, чтобы восстановить историю с уже существующим UUID, временем и статусом
    public Transaction(UUID id, TransactionType type, long amount, String fromAccountId, String toAccountId, LocalDateTime timestamp, Status status) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.timestamp = timestamp;
        this.status = status;
    }

    public UUID getId() { return id; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public TransactionType getType() { return type; }
    public long getAmount() { return amount; }
    public String getFromAccountId() { return fromAccountId; }
    public String getToAccountId() { return toAccountId; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    @Override
    public String toString() {
        return String.format(
                "Transaction{id=%s, type=%s, amount=%d, fromAccountId='%s'%s, status=%s, timestamp=%s}",
                id,
                type,
                amount,
                fromAccountId,
                (toAccountId != null ? ", toAccountId='" + toAccountId + "'" : ""),
                status,
                timestamp
        );
    }
}