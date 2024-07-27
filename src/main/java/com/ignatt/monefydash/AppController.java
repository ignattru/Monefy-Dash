package com.ignatt.monefydash;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class AppController {

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

    @PostMapping("/upload")
    public String uploadCSV(@RequestParam("file") MultipartFile file, Model model) {

        // Создаем импортер и передаем туда файл с формы
        CSVImporter csvImporter = CSVImporter.getImporter();
        List<Transaction> transactions = csvImporter.startImport(file);

        Statistic statistic = new Statistic(transactions);
        System.out.println("statistic содержит " + statistic.getCountTransactions());

        // Информация о загруженном файле
            // Имя файла
            model.addAttribute("fileName", file.getOriginalFilename());

            // Размер файла, килобайты
            model.addAttribute("fileSize", Math.round(file.getSize()/1024.0));

            // Количество записей в файле
            model.addAttribute("fileNumRecords", statistic.getCountTransactions());

        // Таблица всех транзакций
        model.addAttribute("transactions", transactions);

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
