package com.example.quanlychitieu.data.model;

public class Budget {
    private long id;
    private String category;
    private double amount;
    private double spent;

    public Budget(long id, String category, double amount, double spent) {
        this.id = id;
        this.category = category;
        this.amount = amount;
        this.spent = spent;
    }

    public long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public double getSpent() {
        return spent;
    }

    public double getRemaining() {
        return amount - spent;
    }

    public int getProgressPercentage() {
        return amount > 0 ? (int)((spent / amount) * 100) : 0;
    }
}
