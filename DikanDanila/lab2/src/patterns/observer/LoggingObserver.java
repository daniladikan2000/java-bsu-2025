package patterns.observer;

import models.Transaction;

public class LoggingObserver implements TransactionObserver {

    @Override
    public void update(Transaction transaction) {
        System.out.printf(
                "[OBSERVER/LOG] Транзакция %s завершена со статусом: %s. Детали: %s%n",
                transaction.getId(),
                transaction.getStatus(),
                transaction.toString()
        );
    }
}