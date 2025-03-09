package com.baotrongit.tlucontact.ui.directory

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.baotrongit.tlucontact.R
import com.baotrongit.tlucontact.adapter.StaffAdapter
import com.baotrongit.tlucontact.databinding.ActivityStaffDirectoryBinding
import com.baotrongit.tlucontact.data.model.Staff
import com.baotrongit.tlucontact.data.model.UnitType
import com.baotrongit.tlucontact.utils.DataProvider
import kotlinx.coroutines.launch

class StaffDirectoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStaffDirectoryBinding
    private lateinit var staffAdapter: StaffAdapter
    private var allStaff: List<Staff> = emptyList()
    private var currentSortOrder = SortOrder.ASCENDING
    private var currentUnitType: UnitType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffDirectoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupSearchView()
        setupSortSpinner()
        setupFilterSpinner()
        setupSwipeRefresh()

        loadStaff()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        staffAdapter = StaffAdapter(
            emptyList(),
            lifecycleScope
        ) { staff ->
            val intent = Intent(this, StaffDetailActivity::class.java).apply {
                putExtra("STAFF_ID", staff.id)
            }
            startActivity(intent)
        }

        binding.rvStaff.apply {
            layoutManager = LinearLayoutManager(this@StaffDirectoryActivity)
            adapter = staffAdapter
        }
    }

    private fun setupSearchView() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filterStaff(s.toString())
            }
        })

        binding.ivClearSearch.setOnClickListener {
            binding.etSearch.text.clear()
        }
    }

    private fun setupSortSpinner() {
        val sortOptions = arrayOf("Tên A-Z", "Tên Z-A", "Chức vụ A-Z", "Chức vụ Z-A")
        val adapter = ArrayAdapter(this, R.layout.spinner_item, sortOptions)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerSort.adapter = adapter

        binding.spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                currentSortOrder = if (position == 0 || position == 1) {
                    SortOrder.ASCENDING
                } else {
                    SortOrder.DESCENDING
                }
                sortStaff(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupFilterSpinner() {
        val filterOptions = arrayOf("Tất cả", "Khoa", "Phòng", "Ban", "Trung tâm", "Khác")
        val adapter = ArrayAdapter(this, R.layout.spinner_item, filterOptions)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerFilter.adapter = adapter

        binding.spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                currentUnitType = when (position) {
                    0 -> null // Tất cả
                    1 -> UnitType.FACULTY
                    2 -> UnitType.DEPARTMENT
                    3 -> UnitType.OFFICE
                    4 -> UnitType.CENTER
                    5 -> UnitType.OTHER
                    else -> null
                }
                filterStaff(binding.etSearch.text.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(R.color.blue_500)
        binding.swipeRefresh.setOnRefreshListener {
            loadStaff()
        }
    }

    private fun loadStaff() {
        binding.progressBar.visibility = View.VISIBLE
        binding.layoutEmpty.visibility = View.GONE
        binding.rvStaff.visibility = View.GONE

        // Use lifecycleScope to call the suspend function
        lifecycleScope.launch {
            try {
                allStaff = DataProvider.getStaff()

                binding.progressBar.visibility = View.GONE

                if (allStaff.isEmpty()) {
                    binding.layoutEmpty.visibility = View.VISIBLE
                    binding.rvStaff.visibility = View.GONE
                } else {
                    binding.layoutEmpty.visibility = View.GONE
                    binding.rvStaff.visibility = View.VISIBLE
                    staffAdapter.updateData(allStaff)
                    sortStaff(0)
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                binding.layoutEmpty.visibility = View.VISIBLE
                binding.rvStaff.visibility = View.GONE
                Toast.makeText(this@StaffDirectoryActivity, "Lỗi khi tải dữ liệu: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun filterStaff(query: String) {
        staffAdapter.updateData(allStaff.filter { staff ->
            staff.fullName.contains(query, ignoreCase = true) ||
                    staff.position.contains(query, ignoreCase = true) ||
                    staff.id.contains(query, ignoreCase = true)
        })

        if (staffAdapter.itemCount == 0) {
            binding.layoutEmpty.visibility = View.VISIBLE
            binding.rvStaff.visibility = View.GONE
        } else {
            binding.layoutEmpty.visibility = View.GONE
            binding.rvStaff.visibility = View.VISIBLE
        }
    }

    private fun sortStaff(sortOption: Int) {
        when (sortOption) {
            0 -> staffAdapter.updateData(allStaff.sortedBy { it.fullName }) // Tên A-Z
            1 -> staffAdapter.updateData(allStaff.sortedByDescending { it.fullName }) // Tên Z-A
            2 -> staffAdapter.updateData(allStaff.sortedBy { it.position }) // Chức vụ A-Z
            3 -> staffAdapter.updateData(allStaff.sortedByDescending { it.position }) // Chức vụ Z-A
        }
    }

    enum class SortOrder {
        ASCENDING, DESCENDING
    }
}
