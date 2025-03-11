package com.example.quanlychitieu.ui.transactions;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.quanlychitieu.R;
import com.example.quanlychitieu.adapter.TransactionAdapter;
import com.example.quanlychitieu.databinding.FragmentTransactionsBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionsFragment extends Fragment {

    private FragmentTransactionsBinding binding;
    private TransactionsViewModel viewModel;
    private TransactionAdapter adapter;
    private final Calendar fromDateCalendar = Calendar.getInstance();
    private final Calendar toDateCalendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private boolean navigatedFromDashboard = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTransactionsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(TransactionsViewModel.class);

        setupToolbar();

        setupDatePickers();
        setupCategoryFilter();
        setupRecyclerView();
        setupFilterButtons();
        setupAddTransactionButton();

        // Observe transactions data
        observeTransactions();
    }

    private void setupDatePickers() {
        // Set default date range (current month)
        fromDateCalendar.set(Calendar.DAY_OF_MONTH, 1);
        binding.dateFromInput.setText(dateFormatter.format(fromDateCalendar.getTime()));
        binding.dateToInput.setText(dateFormatter.format(toDateCalendar.getTime()));

        // From date picker
        binding.dateFromInput.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        fromDateCalendar.set(year, month, dayOfMonth);
                        binding.dateFromInput.setText(dateFormatter.format(fromDateCalendar.getTime()));
                    },
                    fromDateCalendar.get(Calendar.YEAR),
                    fromDateCalendar.get(Calendar.MONTH),
                    fromDateCalendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        // To date picker
        binding.dateToInput.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        toDateCalendar.set(year, month, dayOfMonth);
                        binding.dateToInput.setText(dateFormatter.format(toDateCalendar.getTime()));
                    },
                    toDateCalendar.get(Calendar.YEAR),
                    toDateCalendar.get(Calendar.MONTH),
                    toDateCalendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    private void setupCategoryFilter() {
        // This would normally come from your database
        List<String> categories = new ArrayList<>();
        categories.add("Tất cả danh mục");
        categories.add("Ăn uống");
        categories.add("Di chuyển");
        categories.add("Mua sắm");
        categories.add("Giải trí");
        categories.add("Hóa đơn");
        categories.add("Khác");

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                categories
        );

        AutoCompleteTextView categoryInput = binding.categoryFilterInput;
        categoryInput.setAdapter(categoryAdapter);
        categoryInput.setText(categories.get(0), false);
    }

    private void setupRecyclerView() {
        adapter = new TransactionAdapter();
        binding.transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.transactionsRecyclerView.setAdapter(adapter);
    }

    private void setupFilterButtons() {
        // Apply filter button
        binding.applyFilterButton.setOnClickListener(v -> {
            Date fromDate = fromDateCalendar.getTime();
            Date toDate = toDateCalendar.getTime();
            String category = binding.categoryFilterInput.getText().toString();

            if (fromDate.after(toDate)) {
                Toast.makeText(requireContext(), "Ngày bắt đầu phải trước ngày kết thúc", Toast.LENGTH_SHORT).show();
                return;
            }

            // Apply filter in ViewModel
            viewModel.applyFilter(fromDate, toDate, category);
        });

        // Clear filter button
        binding.clearFilterButton.setOnClickListener(v -> {
            // Reset date pickers to current month
            fromDateCalendar.setTime(new Date());
            fromDateCalendar.set(Calendar.DAY_OF_MONTH, 1);
            toDateCalendar.setTime(new Date());

            binding.dateFromInput.setText(dateFormatter.format(fromDateCalendar.getTime()));
            binding.dateToInput.setText(dateFormatter.format(toDateCalendar.getTime()));
            binding.categoryFilterInput.setText("Tất cả danh mục", false);

            // Clear filter in ViewModel
            viewModel.clearFilter();
        });
    }

    private void setupAddTransactionButton() {
        binding.fabAddTransaction.setOnClickListener(v -> {
            // Navigate to add transaction screen
            // You would typically use Navigation Component here
            Toast.makeText(requireContext(), "Thêm giao dịch mới", Toast.LENGTH_SHORT).show();
        });
    }

    private void observeTransactions() {
        viewModel.getTransactions().observe(getViewLifecycleOwner(), transactions -> {
            adapter.submitList(transactions);

            // Show/hide empty state
            if (transactions.isEmpty()) {
                binding.emptyState.setVisibility(View.VISIBLE);
                binding.transactionsRecyclerView.setVisibility(View.GONE);
            } else {
                binding.emptyState.setVisibility(View.GONE);
                binding.transactionsRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupToolbar() {
        // Show back button if navigated from dashboard, not from bottom nav
        if (navigatedFromDashboard) {
            binding.toolbar.setNavigationIcon(R.drawable.ic_back);
            binding.toolbar.setNavigationOnClickListener(v -> {
                requireActivity().onBackPressed();
            });
        } else {
            // If we're on the main bottom nav, don't show back button
            binding.toolbar.setNavigationIcon(null);
        }
    }
    private boolean isNavigatedFromBottomNav() {
        // Check if this fragment is one of the main bottom nav destinations
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        return navController.getCurrentDestination() != null &&
                navController.getCurrentDestination().getId() == R.id.navigation_transactions;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
