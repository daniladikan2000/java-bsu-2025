package patterns.visitor;

import models.Transaction;

public interface TransactionVisitor {
    void visit(Transaction transaction);
}