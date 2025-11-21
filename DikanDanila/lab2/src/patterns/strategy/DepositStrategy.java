package patterns.strategy;

import models.Account;
import models.Transaction;
import repository.AccountRepository;
import java.util.Optional;

public class DepositStrategy implements TransactionStrategy {

    @Override
    public void execute(Transaction transaction, AccountRepository repository) {
        String accountId = transaction.getFromAccountId();
        long amount = transaction.getAmount();

        System.out.printf("[STRATEGY] Попытка пополнения счета %s на сумму %d%n", accountId, amount);

        Optional<Account> accountOpt = repository.findById(accountId);

        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            account.getLock().lock();
            try {
                if (account.isFrozen()) {
                    System.err.printf("ОШИБКА: Счет %s заморожен. Операция отклонена.%n", accountId);
                    transaction.setStatus(Transaction.Status.FAILED);
                    return;
                }

                if (account.deposit(amount)) {
                    repository.update(account);
                    System.out.printf("УСПЕХ: Счет %s пополнен. Новый баланс: %d%n", accountId, account.getBalance());
                    transaction.setStatus(Transaction.Status.COMPLETED);
                } else {
                    System.err.printf("ОШИБКА: Не удалось пополнить счет %s.%n", accountId);
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