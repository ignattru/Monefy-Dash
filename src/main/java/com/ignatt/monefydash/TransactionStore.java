package com.ignatt.monefydash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TransactionStore {
    private static List<Transaction> transactions = new ArrayList<>();

    private TransactionStore() {
    }

    public static synchronized List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public static synchronized void setTransactions(List<Transaction> newTransactions) {
        transactions = new ArrayList<>(newTransactions);
    }
}