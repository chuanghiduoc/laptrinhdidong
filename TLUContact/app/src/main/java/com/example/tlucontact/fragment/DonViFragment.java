package com.example.tlucontact.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlucontact.R;
import com.example.tlucontact.adapter.UnitAdapter;
import com.example.tlucontact.model.Unit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DonViFragment extends Fragment {

    private RecyclerView recyclerView;
    private UnitAdapter adapter;
    private AutoCompleteTextView autoCompleteSort;
    private SearchView searchView;
    private TextView tvResultsCount;
    private View emptyState;
    private List<Unit> allUnitList;
    private List<Unit> filteredUnitList;
    private String currentSortOption = "Tên";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_don_vi, container, false);

        // Khởi tạo các view
        recyclerView = view.findViewById(R.id.rcv_units);
        autoCompleteSort = view.findViewById(R.id.auto_complete_sort);
        searchView = view.findViewById(R.id.search_view);
        tvResultsCount = view.findViewById(R.id.tv_results_count);
        emptyState = view.findViewById(R.id.empty_state);

        // Thiết lập RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo dữ liệu
        setupData();

        // Thiết lập dropdown sắp xếp
        setupSortDropdown();

        // Thiết lập chức năng tìm kiếm
        setupSearch();


        return view;
    }

    private void setupData() {
        // Initialize data lists
        allUnitList = new ArrayList<>();
        allUnitList.add(new Unit("Khoa Kinh Tế", "0243xxxxxxx", "456 Đường DEF"));
        allUnitList.add(new Unit("Khoa Công Nghệ Thông Tin", "0243xxxxxxx", "789 Đường ABC"));
        allUnitList.add(new Unit("Khoa Ngoại Ngữ", "0243xxxxxxx", "123 Đường GHI"));
        allUnitList.add(new Unit("Phòng Đào Tạo", "0243xxxxxxx", "321 Đường XYZ"));
        allUnitList.add(new Unit("Phòng Tài Chính", "0243xxxxxxx", "654 Đường LMN"));

        // Tạo danh sách đã lọc (ban đầu chứa tất cả các mục)
        filteredUnitList = new ArrayList<>(allUnitList);

        // Sắp xếp theo tùy chọn mặc định
        sortList(currentSortOption);

        // Thiết lập adapter với danh sách đã lọc
        adapter = new UnitAdapter(filteredUnitList);
        recyclerView.setAdapter(adapter);

        // Cập nhật số lượng kết quả
        updateResultsCount();
    }

    private void setupSortDropdown() {
        ArrayAdapter<CharSequence> dropdownAdapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.sort_options,
                android.R.layout.simple_list_item_1
        );
        autoCompleteSort.setAdapter(dropdownAdapter);

        // Đặt lựa chọn mặc định
        autoCompleteSort.setText(currentSortOption, false);

        autoCompleteSort.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View clickedView, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                currentSortOption = selected;
                sortList(selected);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void sortList(String sortOption) {
        if (sortOption.equals("Tên")) {
            Collections.sort(filteredUnitList, Comparator.comparing(Unit::getName));
        } else if (sortOption.equals("Địa chỉ")) {
            Collections.sort(filteredUnitList, Comparator.comparing(Unit::getAddress));
        }
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterData(newText);
                return true;
            }
        });
    }

    private void filterData(String query) {
        filteredUnitList.clear();

        if (query == null || query.isEmpty()) {
            // Nếu truy vấn rỗng, hiển thị tất cả các mục
            filteredUnitList.addAll(allUnitList);
        } else {
            // Chuyển đổi truy vấn thành chữ thường để so sánh không phân biệt chữ hoa/thường
            String lowercaseQuery = query.toLowerCase().trim();

            // Lọc các mục có chứa chuỗi truy vấn trong tên, số điện thoại hoặc địa chỉ
            for (Unit unit : allUnitList) {
                if (unit.getName().toLowerCase().contains(lowercaseQuery) ||
                        unit.getPhone().toLowerCase().contains(lowercaseQuery) ||
                        unit.getAddress().toLowerCase().contains(lowercaseQuery)) {
                    filteredUnitList.add(unit);
                }
            }
        }

        // Áp dụng tùy chọn sắp xếp hiện tại cho kết quả đã lọc
        sortList(currentSortOption);

        // Cập nhật adapter với dữ liệu đã lọc mới
        adapter.setSearchQuery(query); // Điều này sẽ được sử dụng để đánh dấu văn bản tìm kiếm
        adapter.notifyDataSetChanged();

        // Cập nhật số lượng kết quả
        updateResultsCount();

        // Hiển thị/ẩn trạng thái trống
        updateEmptyState();
    }

    private void updateResultsCount() {
        if (filteredUnitList.size() == allUnitList.size()) {
            tvResultsCount.setText("Hiển thị tất cả đơn vị");
        } else {
            tvResultsCount.setText("Hiển thị " + filteredUnitList.size() + " đơn vị");
        }
    }

    private void updateEmptyState() {
        if (filteredUnitList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }
}
