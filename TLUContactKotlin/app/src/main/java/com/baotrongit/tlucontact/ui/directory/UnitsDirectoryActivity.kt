package com.baotrongit.tlucontact.ui.directory

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.baotrongit.tlucontact.R
import com.baotrongit.tlucontact.adapter.UnitAdapter
import com.baotrongit.tlucontact.databinding.ActivityUnitsDirectoryBinding
import com.baotrongit.tlucontact.data.model.TLUUnit
import com.baotrongit.tlucontact.data.model.UnitType
import com.baotrongit.tlucontact.utils.DataProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class UnitsDirectoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUnitsDirectoryBinding
    private lateinit var unitAdapter: UnitAdapter
    private var allUnits: List<TLUUnit> = emptyList()
    private var currentSortOrder = SortOrder.ASCENDING
    private var currentUnitType: UnitType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUnitsDirectoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupSearchView()
        setupSortSpinner()
        setupFilterSpinner()
        setupSwipeRefresh()

        loadUnits()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }


    private fun setupRecyclerView() {
        unitAdapter = UnitAdapter(emptyList()) { unit ->
            // Xử lý khi người dùng nhấn vào một đơn vị
            val intent = Intent(this, UnitDetailActivity::class.java).apply {
                putExtra("UNIT_ID", unit.id)
            }
            startActivity(intent)
        }

        binding.rvUnits.apply {
            layoutManager = LinearLayoutManager(this@UnitsDirectoryActivity)
            adapter = unitAdapter
        }
    }

    private fun setupSearchView() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filterUnits(s.toString())
            }
        })

        binding.ivClearSearch.setOnClickListener {
            binding.etSearch.text.clear()
        }
    }

    private fun setupSortSpinner() {
        val sortOptions = arrayOf("Tên A-Z", "Tên Z-A")
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
                currentSortOrder = if (position == 0) SortOrder.ASCENDING else SortOrder.DESCENDING
                sortUnits()
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
                filterUnits(binding.etSearch.text.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(R.color.blue_500)
        binding.swipeRefresh.setOnRefreshListener {
            loadUnits()
        }
    }

    private fun loadUnits() {
        // Hiển thị loading
        binding.progressBar.visibility = View.VISIBLE
        binding.layoutEmpty.visibility = View.GONE
        binding.rvUnits.visibility = View.GONE

        // Use lifecycleScope to call the suspend function
        lifecycleScope.launch {
            try {
                allUnits = DataProvider.getUnits()

                // Ẩn loading và hiển thị dữ liệu
                binding.progressBar.visibility = View.GONE

                if (allUnits.isEmpty()) {
                    binding.layoutEmpty.visibility = View.VISIBLE
                    binding.rvUnits.visibility = View.GONE
                } else {
                    binding.layoutEmpty.visibility = View.GONE
                    binding.rvUnits.visibility = View.VISIBLE
                    unitAdapter.updateData(allUnits)
                    sortUnits()
                }
            } catch (e: Exception) {
                // Handle error
                binding.progressBar.visibility = View.GONE
                binding.layoutEmpty.visibility = View.VISIBLE
                binding.rvUnits.visibility = View.GONE
            } finally {
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }
    private fun filterUnits(query: String) {
        unitAdapter.filter(query, currentUnitType)

        // Kiểm tra nếu không có kết quả sau khi lọc
        if (unitAdapter.itemCount == 0) {
            binding.layoutEmpty.visibility = View.VISIBLE
            binding.rvUnits.visibility = View.GONE
        } else {
            binding.layoutEmpty.visibility = View.GONE
            binding.rvUnits.visibility = View.VISIBLE
        }
    }

    private fun sortUnits() {
        unitAdapter.sortByName(currentSortOrder == SortOrder.ASCENDING)
    }

    enum class SortOrder {
        ASCENDING, DESCENDING
    }
}
