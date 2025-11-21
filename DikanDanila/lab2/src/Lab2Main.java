import models.Account;
import models.Transaction;
import models.User;
import patterns.factory.TransactionFactory;
import patterns.observer.LoggingObserver;
import patterns.singleton.DatabaseConnection;
import patterns.visitor.TransactionReportVisitor;
import repository.AccountRepository;
import service.TransactionProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Lab2Main {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Запуск банковской системы");

        DatabaseConnection dbConnection = DatabaseConnection.getInstance();

        AccountRepository accountRepository = new AccountRepository();
        TransactionProcessor transactionProcessor = new TransactionProcessor(accountRepository);

        LoggingObserver loggingObserver = new LoggingObserver();
        transactionProcessor.addObserver(loggingObserver);

        System.out.println("\n2. Создание счетов в базе данных");
        String userId = "USER_WAYNE_001";

        accountRepository.save(new Account("ACC001", userId, 100000));
        accountRepository.save(new Account("ACC002", userId, 50000));
        accountRepository.save(new Account("ACC003", userId, 200000));

        User user = new User("Bruce Wayne", Arrays.asList("ACC001", "ACC002", "ACC003"));
        System.out.println("Создан пользователь: " + user.getNickname());

        System.out.println("\n3. Запуск асинхронной обработки транзакций");
        List<Transaction> requests = new ArrayList<>();

        requests.add(TransactionFactory.createDeposit("ACC001", 2000));       // +2000
        requests.add(TransactionFactory.createWithdrawal("ACC002", 1000));    // -1000
        requests.add(TransactionFactory.createTransfer("ACC003", "ACC001", 5000)); // Перевод
        requests.add(TransactionFactory.createWithdrawal("ACC002", 9999999)); // Ошибка (мало денег)
        requests.add(TransactionFactory.createFreeze("ACC003"));              // Заморозка
        requests.add(TransactionFactory.createDeposit("ACC003", 1000));      // Ошибка (заморожен)

        for (Transaction tx : requests) {
            transactionProcessor.process(tx);
        }

        System.out.println("\nОжидание завершения базовых транзакций...");
        Thread.sleep(2000);

        System.out.println("\n4. Запуск Stress-Test (Deadlock Prevention)");
        List<Transaction> concurrentTx = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            concurrentTx.add(TransactionFactory.createTransfer("ACC001", "ACC002", 10)); // 1 -> 2
            concurrentTx.add(TransactionFactory.createTransfer("ACC002", "ACC001", 10)); // 2 -> 1
        }

        for (Transaction tx : concurrentTx) {
            transactionProcessor.process(tx);
        }

        System.out.println("Ожидание завершения конкурентных транзакций...");
        Thread.sleep(3000);

        System.out.println("\n5. Генерация финального отчета (VISITOR)");
        List<Transaction> historyFromDb = accountRepository.findAllTransactions();

        TransactionReportVisitor reportVisitor = new TransactionReportVisitor();
        String report = reportVisitor.generateReport(historyFromDb);
        System.out.println(report);

        System.out.println("Завершение работы системы");
        transactionProcessor.shutdown();
        dbConnection.closeConnection();
    }
}