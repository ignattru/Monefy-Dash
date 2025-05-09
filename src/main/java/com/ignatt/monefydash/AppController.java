package com.ignatt.monefydash;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Controller
public class AppController {
    private List<Transaction> transactionList = new ArrayList<>(); // Добавленное поле

    @GetMapping("/")
    public String showIndex() {
        return "index";
    }

    @GetMapping("/start")
    public String showForm() {
        return "uploadForm";
    }

    @GetMapping("/donut")
    public String showDonut() {
        return "donut";
    }

    @GetMapping("/sankey-data")
@ResponseBody
public Map<String, Object> getSankeyData() {
    // Получаем данные о расходах по категориям (по модулю)
    TreeMap<String, Double> expenseByCategory = new Statistic(transactionList).getExpenseByCategory(true);

    // Создаем список узлов (nodes)
    List<Map<String, Object>> nodes = new ArrayList<>();
    nodes.add(Map.of(
        "name", "Доход",
        "color", "#399918"  // Зеленый цвет для дохода
    ));

    // Добавляем категории расходов как узлы с рандомными цветами
    for (String category : expenseByCategory.keySet()) {
        nodes.add(Map.of(
            "name", category,
            "color", getRandomColor()
        ));
    }

    // Создаем список связей (links)
    List<Map<String, Object>> links = new ArrayList<>();

    // Добавляем связи от "Дохода" к каждой категории расходов
    for (int i = 1; i < nodes.size(); i++) {
        String category = (String) nodes.get(i).get("name");
        double amount = expenseByCategory.get(category);

        links.add(Map.of(
            "from", "Доход",
            "to", category,
            "weight", amount
        ));
    }

    return Map.of(
        "nodes", nodes,
        "links", links
    );
}

// Метод для генерации случайного цвета в HEX-формате
private String getRandomColor() {
    String[] colors = {
        "#FF7777", "#FFB347", "#FFCC33", "#A2C8FF", "#77DD77",
        "#FFA07A", "#20B2AA", "#9370DB", "#F08080", "#6495ED",
        "#DDA0DD", "#5F9EA0", "#FF7F50", "#4682B4", "#9ACD32"
    };
    return colors[(int) (Math.random() * colors.length)];
}

    @PostMapping("/upload")
    public String uploadCSV(@RequestParam("file") MultipartFile file, Model model) {
        // Создаем импортер и передаем туда файл с формы
        CSVImporter csvImporter = CSVImporter.getImporter();
        this.transactionList = csvImporter.startImport(file);

        Statistic statistic = new Statistic(transactionList);
        System.out.println("statistic содержит " + statistic.getCountTransactions());

        // Информация о загруженном файле
            // Имя файла
            model.addAttribute("fileName", file.getOriginalFilename());

            // Размер файла, килобайты
            model.addAttribute("fileSize", Math.round(file.getSize()/1024.0));

            // Количество записей в файле
            model.addAttribute("fileNumRecords", statistic.getCountTransactions());

        // Таблица всех транзакций
        model.addAttribute("transactions", transactionList);

        // Статистика по транзакциям

        // Общий доход и расход за все время
        model.addAttribute("totalIncome", statistic.getTotalAmountByType(true));
        model.addAttribute("totalExpence", statistic.getTotalAmountByType(false));

        // Самый большой ежемесячный доход и расход
        model.addAttribute("maxMonthlyIncome", statistic.getMaxMinMontlyAmountByType(true,true,false));
        model.addAttribute("maxMonthlyExpence", statistic.getMaxMinMontlyAmountByType(false,false,false));

        // Самый маленький ежемесячный доход и расход
        model.addAttribute("minMonthlyIncome", statistic.getMaxMinMontlyAmountByType(true,false,false));
        model.addAttribute("minMonthlyExpence", statistic.getMaxMinMontlyAmountByType(false,true,false));

        // Для определения границ графка нужно значение максимального ежемесячного расхода по модулю
        model.addAttribute("minMonthlyExpenceAbs", statistic.getMaxMinMontlyAmountByType(false,true,true));

        // Доходы и расходы по месяцам
        model.addAttribute("monthlyIncomeAmountByType", statistic.getMonthlyAmountByType(true,false));
        model.addAttribute("monthlyExpenceAmountByType", statistic.getMonthlyAmountByType(false,false));

        // Доходы и расходы по месяцам
        model.addAttribute("incomeData", statistic.getMonthlyAmountByType(true,true));
        model.addAttribute("expenseData", statistic.getMonthlyAmountByType(false,true));

        // Данные для бублика - расходы по всем категориям
        model.addAttribute("expenseByCategory", statistic.getExpenseByCategory(true));

        // Топ 4 категории расходов за все время
        model.addAttribute("expenseByCategoryTop", statistic.getTopExpenseByCategory(4,true));

        return "dashboard";
    }
}
