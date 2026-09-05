package com.ignatt.monefydash;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CSVImporter {

    private static final Logger logger = LoggerFactory.getLogger(CSVImporter.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public List<Transaction> startImport(MultipartFile file) throws IOException {
        List<Transaction> transactions = new ArrayList<>();
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Файл пуст");
        }

        char delimiter = detectDelimiter(file);
        logger.info("Используем разделитель: '{}'", delimiter);

        try (Reader reader = new InputStreamReader(file.getInputStream());
             CSVReader csvReader = new CSVReaderBuilder(reader)
                     .withCSVParser(new CSVParserBuilder().withSeparator(delimiter).build())
                     .build()) {

            String[] record;
            int lineNumber = 0;
            csvReader.readNext();
            while ((record = csvReader.readNext()) != null) {
                lineNumber++;
                if (record.length < 4) {
                    logger.warn("Строка {}: недостаточно колонок ({}), пропущена", lineNumber, record.length);
                    continue;
                }

                Transaction transaction = new Transaction();
                try {
                    transaction.setDate(record[0]);
                    transaction.setCategory(record[2]);
                    transaction.setAmount(parseAmount(record[3]));
                    transaction.setDescription(record.length > 7 ? record[7] : "");
                    transactions.add(transaction);
                } catch (DateTimeParseException | NumberFormatException e) {
                    logger.warn("Строка {}: ошибка парсинга ({})", lineNumber, e.getMessage());
                }
            }
            logger.info("Импортировано {} транзакций", transactions.size());
        } catch (CsvValidationException e) {
            logger.error("Ошибка валидации CSV", e);
            throw new IOException("Неверный формат CSV", e);
        }
        return transactions;
    }

    private char detectDelimiter(MultipartFile file) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead = file.getInputStream().read(buffer);
        String header = new String(buffer, 0, bytesRead).split("\\r?\\n")[0];

        if (header.contains(";")) {
            return ';';
        } else if (header.contains(",")) {
            return ',';
        } else {
            logger.warn("Разделитель не определён, используем ';' по умолчанию");
            return ';';
        }
    }

    private Double parseAmount(String amountStr) {
        String cleaned = amountStr.replaceAll("[^\\d.,-]", "");
        cleaned = cleaned.replace(',', '.');
        return Double.parseDouble(cleaned);
    }
}