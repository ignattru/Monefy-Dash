package com.ignatt.monefydash;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class Statistic {
    private final List<Transaction> transactionList;

    public Statistic(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    public Integer getCountTransactions() {
        return transactionList.size();
    }

    public TreeMap<String, Double> getMonthlyAmountByType(boolean isIncome, boolean absolute) {
        return transactionList.stream()
                .filter(t -> isIncome ? t.getAmount() >= 0 : t.getAmount() < 0)
                .collect(Collectors.groupingBy(
                        Transaction::getGroupDate,
                        TreeMap::new,
                        Collectors.summingDouble(t -> absolute ? Math.abs(t.getAmount()) : t.getAmount())
                ));
    }

    public Double getTotalAmountByType(boolean isIncome) {
        return transactionList.stream()
                .map(Transaction::getAmount)
                .filter(amount -> isIncome ? amount >= 0 : amount < 0)
                .reduce(0.0, Double::sum);
    }

    public Double getMaxMinMonthlyAmountByType(boolean isIncome, boolean isMax, boolean absolute) {
        Map<YearMonth, Double> monthlySums = transactionList.stream()
                .filter(t -> isIncome ? t.getAmount() >= 0 : t.getAmount() < 0)
                .collect(Collectors.groupingBy(
                        t -> YearMonth.from(t.getDate()),
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        if (monthlySums.isEmpty()) {
            return 0.0;
        }

        DoubleSummaryStatistics stats = monthlySums.values().stream()
                .mapToDouble(Math::abs)
                .summaryStatistics();

        double value = isMax ? stats.getMax() : stats.getMin();
        return absolute ? value : (isIncome ? value : -value);
    }

    public TreeMap<String, Double> getExpenseByCategory(boolean absolute) {
        return transactionList.stream()
                .filter(t -> t.getAmount() < 0)
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        TreeMap::new,
                        Collectors.summingDouble(t -> absolute ? Math.abs(t.getAmount()) : t.getAmount())
                ));
    }

    public TreeMap<String, Double> getTopExpenseByCategory(int topCount, boolean absolute) {
        TreeMap<String, Double> all = getExpenseByCategory(absolute);
        return all.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(topCount)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        TreeMap::new
                ));
    }
}