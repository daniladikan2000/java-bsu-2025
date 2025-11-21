package patterns.observer;

import models.Transaction;

public interface TransactionObserver {
    void update(Transaction transaction);
}
