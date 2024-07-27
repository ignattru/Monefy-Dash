package com.ignatt.monefydash;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Класс с функционалом для импорта CSV файла и помещения в коллекцию, создается по паттерну синглтон - может быть только один экземпляр класса
public class CSVImporter {
    private static CSVImporter importer = new CSVImporter();

    private CSVImporter() {
        // Приватный конструктор, чтобы предотвратить создание экземпляров извне.
    }

    public static CSVImporter getImporter() {
        return importer;
    }

    public List<Transaction> startImport(MultipartFile file) {

        // Чтение CSV файла, создание объектов Transaction и сохранение их в коллекцию
        List<Transaction> transactions = new ArrayList<>();

        try (
                Reader reader = new InputStreamReader(file.getInputStream())) {
            CSVReader csvReader = new CSVReaderBuilder(reader)
                    .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                    .build();

            // Читаем csv, пропускаем первую строку - заголовок, маппим поток строк в структуру данных - класс транзакция в листе.
            transactions = csvReader.readAll().stream()
                    .skip(1)
                    .map(record -> {
                        Transaction transaction = new Transaction();
                        transaction.setDate(record[0]);
                        transaction.setCategory(record[2]);
                        transaction.setAmount(Double.valueOf(record[3].replaceAll("[^\\d.,-]", ""))); // В файле пробелы между числами
                        transaction.setDescription(record[7]);
                        return transaction;
                    })
                    .collect(Collectors.toList());

        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
        return transactions;
    }

}

