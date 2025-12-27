package com.pend.model;

import java.time.LocalDate;

public class Transaction {
    private int id;
    private LocalDate date;
    private double amount;
    private String category;
    private String description;
    private String currency;

    public Transaction(int id, LocalDate date, double amount, String category, String description, String currency) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.category = category == null ? "" : category;
        this.description = description == null ? "" : description;
        this.currency = currency == null ? "USD" : currency;
    }

    public Transaction(LocalDate date, double amount, String category, String description, String currency) {
        this(-1, date, amount, category, description, currency);
    }

    // getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
