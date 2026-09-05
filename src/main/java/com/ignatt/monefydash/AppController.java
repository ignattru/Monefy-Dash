package com.ignatt.monefydash;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class AppController {
    private static final Logger logger = LoggerFactory.getLogger(AppController.class);

    private final CSVImporter csvImporter;
    private final DashboardService dashboardService;

    public AppController(CSVImporter csvImporter, DashboardService dashboardService) {
        this.csvImporter = csvImporter;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String showIndex() {
        return "index";
    }

    @GetMapping("/start")
    public String showForm(Model model) {
        if (!model.containsAttribute("errorMessage")) {
            model.addAttribute("errorMessage", null);
        }
        return "uploadForm";
    }

    @GetMapping("/donut")
    public String showDonut() {
        return "donut";
    }

    @GetMapping("/sankey-data")
    @ResponseBody
    public Map<String, Object> getSankeyData() {
        List<Transaction> transactions = TransactionStore.getTransactions();
        return dashboardService.buildSankeyData(transactions);
    }

    @PostMapping("/upload")
    public String uploadCSV(@RequestParam("file") MultipartFile file,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        logger.info("Загрузка файла: {}, размер: {} bytes", file.getOriginalFilename(), file.getSize());

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Файл не выбран или пуст.");
            return "redirect:/start";
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".csv")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Пожалуйста, загрузите файл в формате CSV.");
            return "redirect:/start";
        }

        try {
            List<Transaction> transactions = csvImporter.startImport(file);
            TransactionStore.setTransactions(transactions);

            if (transactions.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Не удалось импортировать ни одной транзакции. Проверьте формат файла.");
                return "redirect:/start";
            }

            Map<String, Object> dashboardModel = dashboardService.prepareDashboardModel(transactions, file);
            model.addAllAttributes(dashboardModel);

            logger.info("Файл успешно обработан, транзакций: {}", transactions.size());
            return "dashboard";
        } catch (IOException e) {
            logger.error("Ошибка при чтении файла", e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Произошла ошибка при чтении файла. Попробуйте ещё раз.");
            return "redirect:/start";
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при импорте", e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Внутренняя ошибка сервера. Пожалуйста, повторите попытку позже.");
            return "redirect:/start";
        }
    }
}