package com.ignatt.monefydash;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class DashboardService {

    private static final String[] COLORS = {
            "#FF7777", "#FFB347", "#FFCC33", "#A2C8FF", "#77DD77",
            "#FFA07A", "#20B2AA", "#9370DB", "#F08080", "#6495ED",
            "#DDA0DD", "#5F9EA0", "#FF7F50", "#4682B4", "#9ACD32"
    };

    public Map<String, Object> prepareDashboardModel(List<Transaction> transactions, MultipartFile file) {
        Map<String, Object> model = new HashMap<>();
        Statistic statistic = new Statistic(transactions);

        model.put("fileName", file.getOriginalFilename());
        model.put("fileSize", Math.round(file.getSize() / 1024.0));
        model.put("fileNumRecords", statistic.getCountTransactions());

        model.put("transactions", transactions);

        model.put("totalIncome", statistic.getTotalAmountByType(true));
        model.put("totalExpence", statistic.getTotalAmountByType(false));

        model.put("monthlyIncomeAmountByType", statistic.getMonthlyAmountByType(true, false));
        model.put("monthlyExpenceAmountByType", statistic.getMonthlyAmountByType(false, false));

        model.put("incomeData", statistic.getMonthlyAmountByType(true, true));
        model.put("expenseData", statistic.getMonthlyAmountByType(false, true));

        model.put("maxMonthlyIncome", statistic.getMaxMinMonthlyAmountByType(true, true, false));
        model.put("maxMonthlyExpence", statistic.getMaxMinMonthlyAmountByType(false, true, false));
        model.put("minMonthlyIncome", statistic.getMaxMinMonthlyAmountByType(true, false, false));
        model.put("minMonthlyExpence", statistic.getMaxMinMonthlyAmountByType(false, false, false));
        model.put("minMonthlyExpenceAbs", statistic.getMaxMinMonthlyAmountByType(false, false, true));

        model.put("expenseByCategory", statistic.getExpenseByCategory(true));
        model.put("expenseByCategoryTop", statistic.getTopExpenseByCategory(4, true));

        return model;
    }

    public Map<String, Object> buildSankeyData(List<Transaction> transactions) {
        Statistic statistic = new Statistic(transactions);
        TreeMap<String, Double> expenseByCategory = statistic.getExpenseByCategory(true);

        List<Map<String, Object>> nodes = new ArrayList<>();
        nodes.add(Map.of("name", "Доход", "color", "#399918"));

        int colorIndex = 0;
        for (String category : expenseByCategory.keySet()) {
            nodes.add(Map.of(
                    "name", category,
                    "color", COLORS[colorIndex++ % COLORS.length]
            ));
        }

        List<Map<String, Object>> links = new ArrayList<>();
        for (int i = 1; i < nodes.size(); i++) {
            String category = (String) nodes.get(i).get("name");
            double amount = expenseByCategory.get(category);
            links.add(Map.of(
                    "from", "Доход",
                    "to", category,
                    "weight", amount
            ));
        }

        return Map.of("nodes", nodes, "links", links);
    }
}