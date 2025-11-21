package patterns.strategy;

import models.Account;
import models.Transaction;
import repository.AccountRepository;
import java.util.Optional;

public class WithdrawStrategy implements TransactionStrategy {

    @Override
    public void execute(Transaction transaction, AccountRepository repository) {
        String accountId = transaction.getFromAccountId();
        long amount = transaction.getAmount();

        System.out.printf("[STRATEGY] Попытка снятия со счета %s суммы %d%n", accountId, amount);

        Optional<Account> accountOpt = repository.findById(accountId);

        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            account.getLock().lock();
            try {
                if (account.isFrozen()) {
                    System.err.printf("ОШИБКА: Счет %s заморожен.%n", accountId);
                    transaction.setStatus(Transaction.Status.FAILED);
                    return;
                }

                if (account.withdraw(amount)) {
                    repository.update(account);
                    System.out.printf("УСПЕХ: Со счета %s снято %d. Баланс: %d%n", accountId, amount, account.getBalance());
                    transaction.setStatus(Transaction.Status.COMPLETED);
                } else {
                    System.err.printf("ОШИБКА: Недостаточно средств или ошибка снятия.%n");
                    transaction.setStatus(Transaction.Status.FAILED);
                }
            } finally {
                account.getLock().unlock();
            }
        } else {
            System.err.printf("ОШИБКА: Счет %s не найден.%n", accountId);
            transaction.setStatus(Transaction.Status.FAILED);
        }
    }
}