package models;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {

    private final String id;
    private final String userUuid;
    private final AtomicLong balance;
    private volatile boolean isFrozen;
    private final Lock lock = new ReentrantLock();

    public Account(String id, String userUuid, long initialBalance) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID счета не может быть пустым.");
        }
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Начальный баланс не может быть отрицательным.");
        }
        this.id = id;
        this.userUuid = userUuid;
        this.balance = new AtomicLong(initialBalance);
        this.isFrozen = false;
    }

    // Конструктор для восстановления из БД (где уже известен статус заморозки)
    public Account(String id, String userUuid, long balance, boolean isFrozen) {
        this.id = id;
        this.userUuid = userUuid;
        this.balance = new AtomicLong(balance);
        this.isFrozen = isFrozen;
    }

    public String getId() { return id; }
    public String getUserUuid() { return userUuid; }
    public long getBalance() { return balance.get(); }
    public boolean isFrozen() { return isFrozen; }
    public Lock getLock() { return lock; }

    public void setFrozen(boolean frozen) {
        this.isFrozen = frozen;
        System.out.println("Счет " + id + (frozen ? " был заморожен." : " был разморожен."));
    }

    public boolean deposit(long amount) {
        if (isFrozen) {
            System.err.println("Ошибка: Счет " + id + " заморожен. Пополнение невозможно.");
            return false;
        }
        if (amount < 0) {
            System.err.println("Ошибка: Нельзя пополнить счет на отрицательную сумму.");
            return false;
        }
        balance.addAndGet(amount);
        return true;
    }

    public boolean withdraw(long amount) {
        if (isFrozen) {
            System.err.println("Ошибка: Счет " + id + " заморожен. Снятие невозможно.");
            return false;
        }
        if (amount < 0) {
            System.err.println("Ошибка: Нельзя снять отрицательную сумму.");
            return false;
        }

        long currentBalance;
        do {
            currentBalance = balance.get();
            if (currentBalance < amount) {
                System.err.println("Ошибка: Недостаточно средств на счете " + id);
                return false;
            }
        } while (!balance.compareAndSet(currentBalance, currentBalance - amount));

        return true;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", userUuid='" + userUuid + '\'' +
                ", balance=" + balance.get() +
                ", isFrozen=" + isFrozen +
                '}';
    }
}



/*
src/main/java/org/example
├── Lab2Main.java
├── models
│   ├── Account.java
│   ├── Transaction.java
│   ├── TransactionType.java
│   └── User.java
├── patterns
│   ├── command
│   │   ├── Command.java
│   │   └── TransactionCommand.java
│   ├── factory
│   │   └── TransactionFactory.java
│   ├── observer
│   │   ├── LoggingObserver.java
│   │   └── TransactionObserver.java
│   ├── singleton
│   │   └── DatabaseConnection.java
│   ├── strategy
│   │   ├── DepositStrategy.java
│   │   ├── FreezeStrategy.java
│   │   ├── TransactionStrategy.java
│   │   ├── TransferStrategy.java
│   │   └── WithdrawStrategy.java
│   └── visitor
│       ├── TransactionReportVisitor.java
│       └── TransactionVisitor.java
├── repository
│   └── AccountRepository.java
└── service
    ├── AccountService.java
    └── TransactionProcessor.java
 */