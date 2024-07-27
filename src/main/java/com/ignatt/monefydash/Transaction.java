package com.ignatt.monefydash;

import org.springframework.stereotype.Component;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@Component
public class Transaction {
    private Date date;
    private String category;
    private Double amount;
    private String description;

    public Date getDate() {
        return date;
    }

    // Чтение входной даты из csv в нужном формате
    public void setDate(String date) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            this.date = inputDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
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

    public Transaction(Date date, String category, Double amount, String description) {
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.description = description;
    }

    public Transaction() {
    }

    // Формат даты для вывода в таблицу транзакций
    public String getFormattedDate() {
        SimpleDateFormat outDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return outDateFormat.format(date);
    }

    // Формат даты для группировки транзакций по месяцу и году и вывода в таблицу
    public String getGroupDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM");
        return dateFormat.format(date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(date, that.date) && Objects.equals(category, that.category) && Objects.equals(amount, that.amount) && Objects.equals(description, that.description);
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
                ", hashcode='" + hashCode() + '\'' +
                '}';
    }
}
