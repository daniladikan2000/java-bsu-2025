package service;

import models.Transaction;
import models.TransactionType;
import patterns.observer.TransactionObserver;
import patterns.strategy.*;
import repository.AccountRepository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TransactionProcessor {

    private final AccountRepository repository;
    private final Map<TransactionType, TransactionStrategy> strategies;
    private final List<TransactionObserver> observers = new CopyOnWriteArrayList<>();
    private final ExecutorService executorService;

    public TransactionProcessor(AccountRepository repository) {
        this.repository = repository;

        int threads = Runtime.getRuntime().availableProcessors();
        this.executorService = Executors.newFixedThreadPool(threads);
        System.out.printf("[PROCESSOR] Запущен обработчик с пулом на %d потоков.%n", threads);

        this.strategies = Map.of(
                TransactionType.DEPOSIT, new DepositStrategy(),
                TransactionType.WITHDRAW, new WithdrawStrategy(),
                TransactionType.TRANSFER, new TransferStrategy(),
                TransactionType.FREEZE, new FreezeStrategy()
        );
    }

    public void process(Transaction transaction) {
        executorService.submit(() -> {
            try {
                TransactionStrategy strategy = strategies.get(transaction.getType());
                if (strategy != null) {
                    strategy.execute(transaction, repository);
                } else {
                    System.err.println("Критическая ошибка: не найдена стратегия для типа " + transaction.getType());
                    transaction.setStatus(Transaction.Status.FAILED);
                }
            } catch (Exception e) {
                System.err.println("Исключение при обработке транзакции " + transaction.getId());
                e.printStackTrace();
                transaction.setStatus(Transaction.Status.FAILED);
            } finally {
                repository.saveTransaction(transaction);

                notifyObservers(transaction);
            }
        });
    }

    public void addObserver(TransactionObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(TransactionObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(Transaction transaction) {
        observers.forEach(observer -> observer.update(transaction));
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
            System.out.println("[PROCESSOR] Обработчик транзакций остановлен.");
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}