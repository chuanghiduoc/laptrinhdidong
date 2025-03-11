package com.example.quanlychitieu.ui.budget;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.quanlychitieu.data.model.Budget;

import java.util.ArrayList;
import java.util.List;

public class BudgetViewModel extends ViewModel {

    private final MutableLiveData<Double> totalBudget = new MutableLiveData<>();
    private final MutableLiveData<Double> totalSpent = new MutableLiveData<>();
    private final MutableLiveData<Double> remainingAmount = new MutableLiveData<>();
    private final MutableLiveData<Integer> progressPercentage = new MutableLiveData<>();
    private final MutableLiveData<List<Budget>> categoryBudgets = new MutableLiveData<>();

    public BudgetViewModel() {
        // Load initial data
        loadBudgetData();
    }

    private void loadBudgetData() {
        // In a real app, this would come from a repository
        double budgetValue = 5000000;
        double spentValue = 3200000;
        double remainingValue = budgetValue - spentValue;
        int progressValue = (int)((spentValue / budgetValue) * 100);

        totalBudget.setValue(budgetValue);
        totalSpent.setValue(spentValue);
        remainingAmount.setValue(remainingValue);
        progressPercentage.setValue(progressValue);

        // Load sample category budgets
        List<Budget> budgets = new ArrayList<>();

        // Add some sample budgets
        budgets.add(new Budget(1, "Ăn uống", 1500000, 1200000));
        budgets.add(new Budget(2, "Di chuyển", 1000000, 800000));
        budgets.add(new Budget(3, "Mua sắm", 1000000, 700000));
        budgets.add(new Budget(4, "Giải trí", 500000, 300000));
        budgets.add(new Budget(5, "Hóa đơn", 1000000, 200000));

        categoryBudgets.setValue(budgets);
    }

    public LiveData<Double> getTotalBudget() {
        return totalBudget;
    }

    public LiveData<Double> getTotalSpent() {
        return totalSpent;
    }

    public LiveData<Double> getRemainingAmount() {
        return remainingAmount;
    }

    public LiveData<Integer> getProgressPercentage() {
        return progressPercentage;
    }

    public LiveData<List<Budget>> getCategoryBudgets() {
        return categoryBudgets;
    }

    // Method to add a new budget
    public void addBudget(Budget budget) {
        List<Budget> currentList = categoryBudgets.getValue();
        if (currentList != null) {
            List<Budget> updatedList = new ArrayList<>(currentList);
            updatedList.add(budget);
            categoryBudgets.setValue(updatedList);

            // Update total budget amount
            updateTotalBudget();
        }
    }

    // Method to update a budget
    public void updateBudget(Budget budget) {
        List<Budget> currentList = categoryBudgets.getValue();
        if (currentList != null) {
            List<Budget> updatedList = new ArrayList<>();

            for (Budget item : currentList) {
                if (item.getId() == budget.getId()) {
                    updatedList.add(budget);
                } else {
                    updatedList.add(item);
                }
            }

            categoryBudgets.setValue(updatedList);

            // Update total budget amount
            updateTotalBudget();
        }
    }

    // Method to delete a budget
    public void deleteBudget(long budgetId) {
        List<Budget> currentList = categoryBudgets.getValue();
        if (currentList != null) {
            List<Budget> updatedList = new ArrayList<>();

            for (Budget item : currentList) {
                if (item.getId() != budgetId) {
                    updatedList.add(item);
                }
            }

            categoryBudgets.setValue(updatedList);

            // Update total budget amount
            updateTotalBudget();
        }
    }

    // Helper method to update total budget calculations
    private void updateTotalBudget() {
        List<Budget> budgets = categoryBudgets.getValue();
        if (budgets != null) {
            double totalBudgetAmount = 0;
            double totalSpentAmount = 0;

            for (Budget budget : budgets) {
                totalBudgetAmount += budget.getAmount();
                totalSpentAmount += budget.getSpent();
            }

            double remainingAmount = totalBudgetAmount - totalSpentAmount;
            int progressValue = totalBudgetAmount > 0 ?
                    (int)((totalSpentAmount / totalBudgetAmount) * 100) : 0;

            this.totalBudget.setValue(totalBudgetAmount);
            this.totalSpent.setValue(totalSpentAmount);
            this.remainingAmount.setValue(remainingAmount);
            this.progressPercentage.setValue(progressValue);
        }
    }
}
