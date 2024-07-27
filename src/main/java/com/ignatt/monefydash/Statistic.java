package com.ignatt.monefydash;

import java.util.*;
import java.util.stream.Collectors;


// Класс со статистикой, принимает коллекцию транзакций
public class Statistic {
    private List<Transaction> transactionList;

    public Statistic(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    // Всего записей
    public Integer getCountTransactions() {
        return transactionList.size();
    }

    // Доходы по месяцам. Ключем будет строка с датой из класса с транзакциями,
    // !!!!!!!!!ПЕРЕДЕЛАТЬ НА ЛИНКЕД ХЕШ МАП!!!TreeMap лучше чем хешмап здесь, в нем сразу отсротированные по ключам данные, нужен вывод с сортировкой по месяцам
    // Если хотим получить доход за конкретный месяц то лучше хешмап, он быстрее по ключу возвращает
    public TreeMap<String, Double> getMonthlyAmountByType(boolean isIncome, boolean isModule) {
        TreeMap<String, Double> sumByMonth = transactionList.stream()
                .filter(transaction -> {
                    if (isIncome) {
                        return isModule ? transaction.getAmount() > 0 : transaction.getAmount() >= 0;
                    } else {
                        return isModule ? transaction.getAmount() < 0 : transaction.getAmount() <= 0;
                    }
                })
                .collect(Collectors.groupingBy(Transaction::getGroupDate, TreeMap::new, Collectors.summingDouble(transaction -> {
                    if (isModule) {
                        return Math.abs(transaction.getAmount());
                    } else {
                        return transaction.getAmount();
                    }
                })));

        return sumByMonth;
    }

    // Сумма расходов и доходов за все время
    public Double getTotalAmountByType(boolean isIncome) {
        return transactionList.stream()
                .map(Transaction::getAmount)
                .filter(amount -> isIncome ? amount >= 0 : amount < 0)
                .reduce(0.0, Double::sum);
    }

    // Расход и доход за все время самый большой в месяц
    public Double getMaxMinMontlyAmountByType(boolean isIncome, boolean isMax, boolean outputAbsolute) {
        Map<String, Double> monthlyAmountMap = transactionList.stream()
                .filter(transaction -> isIncome ? transaction.getAmount() >= 0 : transaction.getAmount() < 0)
                .collect(Collectors.groupingBy(
                        transaction -> {
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(transaction.getDate());
                            return cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH);
                        },
                        Collectors.summingDouble(Transaction::getAmount)));

        if (isMax) {
            Double maxAmount = monthlyAmountMap.values().stream()
                    .mapToDouble(Math::abs)
                    .max().orElse(0.0);
            return outputAbsolute ? maxAmount : Math.abs(maxAmount);
        } else {
            Double minAmount = monthlyAmountMap.values().stream()
                    .mapToDouble(Math::abs)
                    .min().orElse(0.0);
            return outputAbsolute ? minAmount : Math.abs(minAmount);
        }
    }

    // Перечень категорий и доходов по ним з все время (можно по модулю)
    public TreeMap<String, Double> getExpenseByCategory(boolean isModule) {
        TreeMap<String, Double> sumByCategory = transactionList.stream()
                .filter(transaction -> isModule ? transaction.getAmount() < 0 : transaction.getAmount() <= 0)
                .collect(Collectors.groupingBy(Transaction::getCategory, TreeMap::new, Collectors.summingDouble(transaction -> {
                    if (isModule) {
                        return Math.abs(transaction.getAmount());
                    } else {
                        return transaction.getAmount();
                    }
                })));
        return sumByCategory;
    }

    // Топ 4 категории расходов за все время
    public TreeMap<String, Double> getTopExpenseByCategory(int topCount, boolean isModule) {
        TreeMap<String, Double> sumByCategory = transactionList.stream()
                .filter(transaction -> isModule ? transaction.getAmount() < 0 : transaction.getAmount() <= 0)
                .collect(Collectors.groupingBy(Transaction::getCategory, TreeMap::new, Collectors.summingDouble(transaction -> {
                    if (isModule) {
                        return Math.abs(transaction.getAmount());
                    } else {
                        return transaction.getAmount();
                    }
                })));

        if (topCount > 0) {
            return sumByCategory.entrySet().stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .limit(topCount)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, TreeMap::new));
        }

        return sumByCategory;
    }
}
