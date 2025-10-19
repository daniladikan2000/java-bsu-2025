import Analyzers.ProdAnalyzer;
import ProductionLines.ChemicalLine;
import ProductionLines.ElectronicsLine;
import ProductionLines.MechanicalLine;
import ProductionLines.ProductionLine;
import Products.ChemicalProduct;
import Products.ElectronicProduct;
import Products.MechanicalProduct;
import Products.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        ElectronicProduct p1_phone = new ElectronicProduct("E01", "Смартфон", 120);
        ElectronicProduct p2_laptop = new ElectronicProduct("E02", "Ноутбук", 180);
        ElectronicProduct p3_tablet = new ElectronicProduct("E03", "Планшет", 150);

        ChemicalProduct c1_fertilizer = new ChemicalProduct("C01", "Удобрение", 60);
        ChemicalProduct c2_plastic = new ChemicalProduct("C02", "Пластик", 90);

        MechanicalProduct m1_engine = new MechanicalProduct("M01", "Двигатель", 300);
        MechanicalProduct m2_gear = new MechanicalProduct("M02", "Шестерня", 45);

        ElectronicsLine electronicsLine = new ElectronicsLine(
                "EL-1 (Электроника)",
                List.of(p1_phone, p2_laptop, p3_tablet),
                0.95
        );

        ChemicalLine chemicalLine = new ChemicalLine(
                "CH-1 (Химия)",
                List.of(c1_fertilizer, c2_plastic),
                0.88
        );

        MechanicalLine mechanicalLine = new MechanicalLine(
                "ME-1 (Механика)",
                List.of(m1_engine, m2_gear),
                0.91
        );

        List<ProductionLine<? extends Product>> allLines = new ArrayList<>();
        allLines.add(electronicsLine);
        allLines.add(chemicalLine);
        allLines.add(mechanicalLine);

        ProdAnalyzer analyzer = new ProdAnalyzer(allLines);
        System.out.println("Данные успешно созданы.\n");

        // Тест 1: Получение линий с эффективностью выше порога (стандартный случай)
        runTest("Тест 1: Линии с эффективностью выше 0.90", () -> {
            double efficiencyThreshold = 0.90;
            List<String> highEfficiencyLines = analyzer.getHighEfficiencyLines(efficiencyThreshold);
            System.out.println("Ожидаемый результат: [EL-1 (Электроника), ME-1 (Механика)]");
            System.out.println("Фактический результат: " + highEfficiencyLines);
            assert highEfficiencyLines.contains("EL-1 (Электроника)") && highEfficiencyLines.contains("ME-1 (Механика)");
        });

        // Тест 2: Получение линий с эффективностью выше порога (пограничный случай, нет совпадений)
        runTest("Тест 2: Линии с эффективностью выше 0.99 (нет совпадений)", () -> {
            double efficiencyThreshold = 0.99;
            List<String> highEfficiencyLines = analyzer.getHighEfficiencyLines(efficiencyThreshold);
            System.out.println("Ожидаемый результат: []");
            System.out.println("Фактический результат: " + highEfficiencyLines);
            assert highEfficiencyLines.isEmpty();
        });

        // Тест 3: Подсчет общего количества изделий по категориям
        runTest("Тест 3: Подсчет продуктов по категориям", () -> {
            Map<String, Long> counts = analyzer.countProductsByCategory();
            counts.forEach((category, count) -> System.out.println("- Категория '" + category + "': " + count + " шт."));
            assert counts.get("Electronic") == 3 && counts.get("Chemical") == 2 && counts.get("Mechanical") == 2;
        });

        // Тест 4: Поиск самой загруженной линии (стандартный случай)
        runTest("Тест 4: Поиск самой загруженной линии", () -> {
            Optional<ProductionLine<? extends Product>> mostLoadedLine = analyzer.findMostLoadedLine();
            mostLoadedLine.ifPresentOrElse(
                    line -> {
                        System.out.println("Ожидаемый результат: Линия 'EL-1 (Электроника)' с 3 продуктами.");
                        System.out.println("Фактический результат: Линия '" + line.getLineId() + "' с " + line.getProducts().size() + " продуктами.");
                        assert line.getLineId().equals("EL-1 (Электроника)");
                    },
                    () -> System.out.println("Самая загруженная линия не найдена.")
            );
        });

        // Тест 5: Поиск самой загруженной линии (пограничный случай, нет линий)
        runTest("Тест 5: Поиск самой загруженной линии в пустом списке", () -> {
            ProdAnalyzer emptyAnalyzer = new ProdAnalyzer(Collections.emptyList());
            Optional<ProductionLine<? extends Product>> mostLoadedLine = emptyAnalyzer.findMostLoadedLine();
            System.out.println("Ожидаемый результат: Optional.empty");
            System.out.println("Фактический результат: " + (mostLoadedLine.isPresent() ? mostLoadedLine.get() : "Optional.empty"));
            assert mostLoadedLine.isEmpty();
        });

        // Тест 6: Сбор всех изделий со всех линий в один список
        runTest("Тест 6: Сбор всех продуктов со всех линий", () -> {
            List<Product> allProducts = analyzer.getAllProductsFromLines();
            System.out.println("Ожидаемый результат: Всего собрано 7 продуктов.");
            System.out.println("Фактический результат: Всего собрано " + allProducts.size() + " продуктов.");
            assert allProducts.size() == 7;
        });

        // Тест 7: Расчет общего времени производства всех изделий
        runTest("Тест 7 (ADVANCED): Расчет общего времени производства", () -> {
            double totalTime = analyzer.calculateTotalProductionTime();
            // 120+180+150 (E) + 60+90 (C) + 300+45 (M) = 450 + 150 + 345 = 945
            System.out.println("Ожидаемый результат: 945.0 минут.");
            System.out.println("Фактический результат: " + totalTime + " минут.");
            assert totalTime == 945.0;
        });

        // Тест 8: Проверка логики метода canProduce
        runTest("Тест 8: Проверка совместимости продуктов с линией (canProduce)", () -> {
            boolean canProduceElectronic = electronicsLine.canProduce(p1_phone); // true
            boolean cannotProduceChemical = electronicsLine.canProduce(c1_fertilizer); // false
            System.out.println("Может ли линия электроники производить смартфон? -> " + canProduceElectronic);
            System.out.println("Может ли линия электроники производить удобрение? -> " + cannotProduceChemical);
            assert canProduceElectronic && !cannotProduceChemical;
        });

        // Тест 9: Проверка валидации (создание линии с некорректной эффективностью)
        runTest("Тест 9 (ADVANCED): Валидация входных данных (эффективность > 1.0)", () -> {
            try {
                new ElectronicsLine("BadLine", List.of(p1_phone), 1.3);
                System.out.println("Тест провален: IllegalArgumentException не было брошено.");
            } catch (IllegalArgumentException e) {
                System.out.println("Тест пройден. Успешно поймано исключение: " + e.getMessage());
                assert e.getMessage().contains("Efficiency must be between 0.0 and 1.0");
            }
        });
    }

    private static void runTest(String testName, Runnable testLogic) {
        System.out.println("=====================================================");
        System.out.println(testName);
        System.out.println("-----------------------------------------------------");
        testLogic.run();
        System.out.println("=====================================================\n");
    }
}