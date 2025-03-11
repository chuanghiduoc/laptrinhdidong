package com.example.quanlychitieu.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlychitieu.R;
import com.example.quanlychitieu.data.model.Transaction;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class TransactionAdapter extends ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder> {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault());
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public TransactionAdapter() {
        super(new DiffUtil.ItemCallback<Transaction>() {
            @Override
            public boolean areItemsTheSame(@NonNull Transaction oldItem, @NonNull Transaction newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Transaction oldItem, @NonNull Transaction newItem) {
                return oldItem.getDescription().equals(newItem.getDescription()) &&
                        oldItem.getAmount() == newItem.getAmount() &&
                        oldItem.getCategory().equals(newItem.getCategory()) &&
                        oldItem.getDate().equals(newItem.getDate());
            }
        });
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = getItem(position);
        holder.bind(transaction);
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        private final ImageView categoryIcon;
        private final TextView titleTextView;
        private final TextView amountTextView;
        private final TextView dateTextView;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryIcon = itemView.findViewById(R.id.category_icon);
            titleTextView = itemView.findViewById(R.id.transaction_title);
            amountTextView = itemView.findViewById(R.id.transaction_amount);
            dateTextView = itemView.findViewById(R.id.transaction_date);
        }

        public void bind(Transaction transaction) {
            // Set transaction title (description)
            titleTextView.setText(transaction.getDescription());

            // Format and set date
            dateTextView.setText(DATE_FORMAT.format(transaction.getDate()));

            // Format currency amount
            String formattedAmount = CURRENCY_FORMAT.format(transaction.getAmount())
                    .replace("₫", "đ")  // Replace currency symbol if needed
                    .replace(",", "."); // Adjust formatting if needed

            // Determine if it's an expense or income and set the appropriate sign and color
            if (transaction.getAmount() < 0) {
                // Expense (negative amount)
                amountTextView.setText(formattedAmount); // Already has the negative sign
                amountTextView.setTextColor(Color.parseColor("#F44336")); // Red color
            } else {
                // Income (positive amount)
                amountTextView.setText("+" + formattedAmount);
                amountTextView.setTextColor(Color.parseColor("#4CAF50")); // Green color
            }

            // Set category icon based on category name
            setCategoryIcon(transaction.getCategory());
        }

        private void setCategoryIcon(String category) {
            // Set the appropriate icon based on the category
            // This is an example implementation - adjust according to your available drawables
            int iconResId;

            switch (category.toLowerCase()) {
                case "ăn uống":
                    iconResId = R.drawable.ic_food;
                    break;
                case "di chuyển":
                    iconResId = R.drawable.ic_food;
                    break;
                case "mua sắm":
                    iconResId = R.drawable.ic_food;
                    break;
                case "giải trí":
                    iconResId = R.drawable.ic_food;
                    break;
                case "hóa đơn":
                    iconResId = R.drawable.ic_food;
                    break;
                default:
                    iconResId = R.drawable.ic_food;
                    break;
            }

            categoryIcon.setImageResource(iconResId);
        }
    }
}
