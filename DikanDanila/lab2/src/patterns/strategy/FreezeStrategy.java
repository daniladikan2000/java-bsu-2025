package patterns.strategy;

import models.Account;
import models.Transaction;
import repository.AccountRepository;
import java.util.Optional;

public class FreezeStrategy implements TransactionStrategy {

    @Override
    public void execute(Transaction transaction, AccountRepository repository) {
        String accountId = transaction.getFromAccountId();
        System.out.printf("[STRATEGY] Заморозка счета %s%n", accountId);

        Optional<Account> accountOpt = repository.findById(accountId);

        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            account.getLock().lock();
            try {
                account.setFrozen(true);
                repository.update(account);

                transaction.setStatus(Transaction.Status.COMPLETED);
                System.out.println("УСПЕХ: Счет заморожен.");
            } finally {
                account.getLock().unlock();
            }
        } else {
            transaction.setStatus(Transaction.Status.FAILED);
        }
    }
}