package com.example.quanlychitieu.data.model;

import java.util.Date;

public class Transaction {
    private long id;
    private String description;
    private double amount;
    private String category;
    private Date date;

    public Transaction(long id, String description, double amount, String category, Date date) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public Date getDate() {
        return date;
    }
}