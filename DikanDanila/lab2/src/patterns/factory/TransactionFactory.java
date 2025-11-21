package patterns.factory;

import models.Transaction;
import models.TransactionType;

public final class TransactionFactory {
    private TransactionFactory() {}

    public static Transaction createDeposit(String accountId, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма пополнения должна быть положительной.");
        }
        return new Transaction(TransactionType.DEPOSIT, amount, accountId);
    }

    public static Transaction createWithdrawal(String accountId, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма снятия должна быть положительной.");
        }
        return new Transaction(TransactionType.WITHDRAW, amount, accountId);
    }

    public static Transaction createTransfer(String fromAccountId, String toAccountId, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма перевода должна быть положительной.");
        }
        if (fromAccountId == null || toAccountId == null || fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("Счет отправителя и получателя не могут совпадать или быть null.");
        }
        return new Transaction(TransactionType.TRANSFER, amount, fromAccountId, toAccountId);
    }

    public static Transaction createFreeze(String accountId) {
        return new Transaction(TransactionType.FREEZE, 0, accountId);
    }
}