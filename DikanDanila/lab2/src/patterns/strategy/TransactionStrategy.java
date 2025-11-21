package patterns.strategy;

import models.Transaction;
import repository.AccountRepository;

public interface TransactionStrategy {
    void execute(Transaction transaction, AccountRepository repository);
}