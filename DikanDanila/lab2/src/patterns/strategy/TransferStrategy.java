package patterns.strategy;

import models.Account;
import models.Transaction;
import repository.AccountRepository;
import java.util.Optional;

public class TransferStrategy implements TransactionStrategy {

    @Override
    public void execute(Transaction transaction, AccountRepository repository) {
        String fromId = transaction.getFromAccountId();
        String toId = transaction.getToAccountId();
        long amount = transaction.getAmount();

        System.out.printf("[STRATEGY] Перевод %d со счета %s на счет %s%n", amount, fromId, toId);

        Optional<Account> fromOpt = repository.findById(fromId);
        Optional<Account> toOpt = repository.findById(toId);

        if (fromOpt.isPresent() && toOpt.isPresent()) {
            Account from = fromOpt.get();
            Account to = toOpt.get();

            from.getLock().lock();
            to.getLock().lock();

            try {
                if (from.isFrozen() || to.isFrozen()) {
                    System.err.println("ОШИБКА: Один из счетов заморожен.");
                    transaction.setStatus(Transaction.Status.FAILED);
                    return;
                }

                if (from.withdraw(amount)) {
                    to.deposit(amount);

                    repository.update(from);
                    repository.update(to);

                    transaction.setStatus(Transaction.Status.COMPLETED);
                    System.out.println("УСПЕХ: Перевод выполнен.");
                } else {
                    System.err.println("ОШИБКА: Недостаточно средств у отправителя.");
                    transaction.setStatus(Transaction.Status.FAILED);
                }
            } finally {
                to.getLock().unlock();
                from.getLock().unlock();
            }
        } else {
            System.err.println("ОШИБКА: Один из счетов не найден.");
            transaction.setStatus(Transaction.Status.FAILED);
        }
    }
}