package service;

import models.Account;
import models.Transaction;
import patterns.command.TransactionCommand;
import patterns.factory.TransactionFactory;
import patterns.observer.LoggingObserver;
import patterns.visitor.TransactionReportVisitor;
import repository.AccountRepository;

import java.util.List;

public class AccountService {

    private final AccountRepository repository;
    private final TransactionProcessor processor;

    public AccountService() {
        this.repository = new AccountRepository();
        this.processor = new TransactionProcessor(repository);

        this.processor.addObserver(new LoggingObserver());
    }

    public void createAccount(String userUuid, long initialBalance) {
        String accountId = java.util.UUID.randomUUID().toString();
        Account account = new Account(accountId, userUuid, initialBalance);
        repository.save(account);
        System.out.println("Создан счет: " + accountId + " для пользователя: " + userUuid);
    }

    public void deposit(String accountId, long amount) {
        Transaction tx = TransactionFactory.createDeposit(accountId, amount);
        submitTransaction(tx);
    }

    public void withdraw(String accountId, long amount) {
        Transaction tx = TransactionFactory.createWithdrawal(accountId, amount);
        submitTransaction(tx);
    }

    public void transfer(String fromId, String toId, long amount) {
        Transaction tx = TransactionFactory.createTransfer(fromId, toId, amount);
        submitTransaction(tx);
    }

    public void freeze(String accountId) {
        Transaction tx = TransactionFactory.createFreeze(accountId);
        submitTransaction(tx);
    }

    public void printAccountStatement() {
        System.out.println("Генерация отчета...");
        List<Transaction> history = repository.findAllTransactions();

        if (history.isEmpty()) {
            System.out.println("История пуста.");
            return;
        }

        TransactionReportVisitor visitor = new TransactionReportVisitor();
        String report = visitor.generateReport(history);
        System.out.println(report);
    }

    private void submitTransaction(Transaction tx) {
        TransactionCommand command = new TransactionCommand(tx, processor);
        command.execute();
    }

    public void close() {
        processor.shutdown();
    }
}