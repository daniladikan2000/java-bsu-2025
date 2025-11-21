package patterns.command;

import models.Transaction;
import service.TransactionProcessor;


public class TransactionCommand implements Command {

    private final Transaction transaction;
    private final TransactionProcessor processor;

    public TransactionCommand(Transaction transaction, TransactionProcessor processor) {
        if (transaction == null || processor == null) {
            throw new IllegalArgumentException("Транзакция и обработчик не могут быть null.");
        }
        this.transaction = transaction;
        this.processor = processor;
    }

    @Override
    public void execute() {
        System.out.println("[COMMAND] Выполнение команды для транзакции: " + transaction.getId());
        processor.process(transaction);
    }
}