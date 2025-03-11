package com.example.quanlychitieu.ui.transactions;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.quanlychitieu.data.model.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionsViewModel extends ViewModel {

    // This will hold all transactions
    private final List<Transaction> allTransactions = new ArrayList<>();

    // This will hold filtered transactions
    private final MutableLiveData<List<Transaction>> transactions = new MutableLiveData<>();

    // Filter parameters
    private Date fromDate;
    private Date toDate;
    private String category;

    public TransactionsViewModel() {
        // Initialize with default data
        loadTransactions();

        // Set initial filter to show all transactions
        clearFilter();
    }

    // For demo purposes, load some sample transactions
    private void loadTransactions() {
        // In a real app, you would load this from a repository or database
        // This is just sample data
        allTransactions.clear();

        // Add some sample transactions
        // In a real app, you would have a proper Transaction class with more fields
        allTransactions.add(new Transaction(1, "Tiền ăn trưa", 50000, "Ăn uống", new Date()));
        allTransactions.add(new Transaction(2, "Xăng xe", 100000, "Di chuyển", new Date()));
        allTransactions.add(new Transaction(3, "Mua quần áo", 500000, "Mua sắm", new Date()));

        // Apply filter to update the LiveData
        applyFilterInternal();
    }

    public LiveData<List<Transaction>> getTransactions() {
        return transactions;
    }

    public void applyFilter(Date fromDate, Date toDate, String category) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.category = category;

        applyFilterInternal();
    }

    public void clearFilter() {
        // Reset to default filter (current month, all categories)
        Date now = new Date();

        // Set fromDate to first day of current month
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(now);
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
        this.fromDate = calendar.getTime();

        // Set toDate to current date
        this.toDate = now;

        // Set category to all
        this.category = "Tất cả danh mục";

        applyFilterInternal();
    }

    private void applyFilterInternal() {
        List<Transaction> filteredList = allTransactions;

        // Apply date filter
        if (fromDate != null && toDate != null) {
            filteredList = filteredList.stream()
                    .filter(transaction ->
                            !transaction.getDate().before(fromDate) &&
                                    !transaction.getDate().after(toDate))
                    .collect(Collectors.toList());
        }

        // Apply category filter
        if (category != null && !category.equals("Tất cả danh mục")) {
            filteredList = filteredList.stream()
                    .filter(transaction -> transaction.getCategory().equals(category))
                    .collect(Collectors.toList());
        }

        transactions.setValue(filteredList);
    }

    // Method to add a new transaction
    public void addTransaction(Transaction transaction) {
        allTransactions.add(transaction);
        applyFilterInternal();
    }
}
