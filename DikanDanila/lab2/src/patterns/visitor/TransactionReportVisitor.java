package patterns.visitor;

import models.Transaction;
import models.TransactionType;

import java.util.List;

public class TransactionReportVisitor implements TransactionVisitor {

    private final StringBuilder reportBuilder = new StringBuilder();
    private int transactionCount = 0;
    private long totalAmountProcessed = 0;

    public TransactionReportVisitor() {
        reportBuilder.append("================ ОТЧЕТ ПО ТРАНЗАКЦИЯМ ================\n");
        reportBuilder.append(String.format("%-37s | %-12s | %-15s | %-10s\n", "ID ТРАНЗАКЦИИ", "ТИП", "СУММА", "СТАТУС"));
        reportBuilder.append("-".repeat(85)).append("\n");
    }

    @Override
    public void visit(Transaction transaction) {
        reportBuilder.append(String.format("%-37s | %-12s | %-15d | %-10s\n",
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getStatus()));
        transactionCount++;

        if (transaction.getType() == TransactionType.DEPOSIT ||
                transaction.getType() == TransactionType.WITHDRAW ||
                transaction.getType() == TransactionType.TRANSFER) {
            totalAmountProcessed += transaction.getAmount();
        }
    }

    public String generateReport(List<Transaction> transactions) {
        for (Transaction tx : transactions) {
            this.visit(tx);
        }
        return getFinalReport();
    }

    private String getFinalReport() {
        reportBuilder.append("-".repeat(85)).append("\n");
        reportBuilder.append(String.format("Всего транзакций: %d\n", transactionCount));
        reportBuilder.append(String.format("Общий оборот (сумма операций): %d\n", totalAmountProcessed));
        reportBuilder.append("================ КОНЕЦ ОТЧЕТА ================\n");
        return reportBuilder.toString();
    }
}