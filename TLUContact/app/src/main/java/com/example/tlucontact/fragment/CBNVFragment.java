package com.example.tlucontact.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlucontact.R;
import com.example.tlucontact.adapter.CBNVAdapter;
import com.example.tlucontact.model.CBNV;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CBNVFragment extends Fragment {

    private RecyclerView recyclerView;
    private CBNVAdapter adapter;
    private AutoCompleteTextView autoCompleteSortCBNV;
    private SearchView searchView;
    private TextView tvResultsCount;
    private View emptyState;
    private List<CBNV> allCbnvList;
    private List<CBNV> filteredCbnvList;
    private String currentSortOption = "Họ và tên";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cbnv, container, false);

        // Khởi tạo các view
        recyclerView = view.findViewById(R.id.rcv_cbns);
        autoCompleteSortCBNV = view.findViewById(R.id.auto_complete_sort_cbnv);
        searchView = view.findViewById(R.id.search_view_cbnv);
        tvResultsCount = view.findViewById(R.id.tv_results_count_cbnv);
        emptyState = view.findViewById(R.id.empty_state_cbnv);

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
        // Khởi tạo danh sách dữ liệu
        allCbnvList = new ArrayList<>();
        allCbnvList.add(new CBNV("Nguyễn Văn Anh", "Giảng viên", "0243.8522.123", "nguyenvananh@tlu.edu.vn", "Khoa CNTT"));
        allCbnvList.add(new CBNV("Trần Thị Bình", "Trưởng khoa", "0243.8522.456", "tranthibinh@tlu.edu.vn", "Khoa Kinh Tế"));
        allCbnvList.add(new CBNV("Lê Văn Cường", "Phó trưởng khoa", "0243.8522.789", "levancuong@tlu.edu.vn", "Khoa CNTT"));
        allCbnvList.add(new CBNV("Phạm Thị Dung", "Giảng viên", "0243.8522.147", "phamthidung@tlu.edu.vn", "Khoa Ngoại Ngữ"));
        allCbnvList.add(new CBNV("Hoàng Văn Eo", "Trưởng phòng", "0243.8522.258", "hoangvaneo@tlu.edu.vn", "Phòng Đào Tạo"));
        allCbnvList.add(new CBNV("Ngô Thị Phương", "Nhân viên", "0243.8522.369", "ngothiphuong@tlu.edu.vn", "Phòng Công Tác Sinh Viên"));
        allCbnvList.add(new CBNV("Vũ Văn Giáp", "Giảng viên", "0243.8522.159", "vuvangiap@tlu.edu.vn", "Khoa Quản Trị Kinh Doanh"));
        allCbnvList.add(new CBNV("Đinh Thị Hương", "Giám đốc trung tâm", "0243.8522.357", "dinhthihuong@tlu.edu.vn", "Trung Tâm Thư Viện"));
        allCbnvList.add(new CBNV("Bùi Văn Ích", "Phó trưởng khoa", "0243.8522.486", "buivanich@tlu.edu.vn", "Khoa Luật"));
        allCbnvList.add(new CBNV("Đỗ Thị Kim", "Kế toán viên", "0243.8522.951", "dothikim@tlu.edu.vn", "Phòng Kế Hoạch Tài Chính"));

        // Tạo danh sách đã lọc (ban đầu chứa tất cả các mục)
        filteredCbnvList = new ArrayList<>(allCbnvList);

        // Sắp xếp theo tùy chọn mặc định
        sortList(currentSortOption);

        // Thiết lập adapter với danh sách đã lọc
        adapter = new CBNVAdapter(filteredCbnvList);
        recyclerView.setAdapter(adapter);

        // Cập nhật số lượng kết quả
        updateResultsCount();
    }

    private void setupSortDropdown() {
        ArrayAdapter<CharSequence> dropdownAdapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.sort_options_cbnv,
                android.R.layout.simple_list_item_1
        );
        autoCompleteSortCBNV.setAdapter(dropdownAdapter);

        // Đặt lựa chọn mặc định
        autoCompleteSortCBNV.setText(currentSortOption, false);

        autoCompleteSortCBNV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View selectedView, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                currentSortOption = selected;
                sortList(selected);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void sortList(String sortOption) {
        if (sortOption.equals("Họ và tên")) {
            Collections.sort(filteredCbnvList, Comparator.comparing(CBNV::getName));
        } else if (sortOption.equals("Chức vụ")) {
            Collections.sort(filteredCbnvList, Comparator.comparing(CBNV::getPosition));
        } else if (sortOption.equals("Đơn vị")) {
            Collections.sort(filteredCbnvList, Comparator.comparing(CBNV::getUnit));
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
        filteredCbnvList.clear();

        if (query == null || query.isEmpty()) {
            // Nếu truy vấn rỗng, hiển thị tất cả các mục
            filteredCbnvList.addAll(allCbnvList);
        } else {
            // Chuyển đổi truy vấn thành chữ thường để so sánh không phân biệt chữ hoa/thường
            String lowercaseQuery = query.toLowerCase().trim();

            // Lọc các mục có chứa chuỗi truy vấn trong bất kỳ trường nào
            for (CBNV cbnv : allCbnvList) {
                if (cbnv.getName().toLowerCase().contains(lowercaseQuery) ||
                        cbnv.getPosition().toLowerCase().contains(lowercaseQuery) ||
                        cbnv.getPhone().toLowerCase().contains(lowercaseQuery) ||
                        cbnv.getEmail().toLowerCase().contains(lowercaseQuery) ||
                        cbnv.getUnit().toLowerCase().contains(lowercaseQuery)) {
                    filteredCbnvList.add(cbnv);
                }
            }
        }

        // Áp dụng tùy chọn sắp xếp hiện tại cho kết quả đã lọc
        sortList(currentSortOption);

        // Cập nhật adapter với dữ liệu đã lọc mới
        adapter.setSearchQuery(query);
        adapter.notifyDataSetChanged();

        // Cập nhật số lượng kết quả
        updateResultsCount();

        // Hiển thị/ẩn trạng thái trống
        updateEmptyState();
    }

    private void updateResultsCount() {
        if (filteredCbnvList.size() == allCbnvList.size()) {
            tvResultsCount.setText("Hiển thị tất cả nhân viên");
        } else {
            tvResultsCount.setText("Hiển thị " + filteredCbnvList.size() + " nhân viên");
        }
    }

    private void updateEmptyState() {
        if (filteredCbnvList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }
}
