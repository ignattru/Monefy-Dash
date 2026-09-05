package com.ignatt.monefydash;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Transaction {
    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter GROUP_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM");

    private LocalDate date;
    private String category;
    private Double amount;
    private String description;

    public Transaction() {
    }

    public Transaction(LocalDate date, String category, Double amount, String description) {
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(String dateStr) {
        this.date = LocalDate.parse(dateStr, INPUT_FORMATTER);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormattedDate() {
        return date != null ? date.format(OUTPUT_FORMATTER) : "";
    }

    public String getGroupDate() {
        return date != null ? date.format(GROUP_FORMATTER) : "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(date, that.date) &&
                Objects.equals(category, that.category) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, category, amount, description);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "date=" + date +
                ", category='" + category + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                '}';
    }
}