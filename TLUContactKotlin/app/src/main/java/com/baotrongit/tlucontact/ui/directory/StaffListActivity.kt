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
import com.baotrongit.tlucontact.data.model.Staff
import com.baotrongit.tlucontact.databinding.ActivityStaffListBinding
import com.baotrongit.tlucontact.utils.DataProvider
import com.baotrongit.tlucontact.utils.UserManager
import kotlinx.coroutines.launch

class StaffListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStaffListBinding
    private lateinit var staffAdapter: StaffAdapter
    private var allStaff: List<Staff> = emptyList()
    private var filteredStaff: List<Staff> = emptyList()
    private var currentSortOrder = SortOrder.ASCENDING
    private var unitId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        unitId = intent.getStringExtra("UNIT_ID")
        if (unitId == null) {
            finish()
            return
        }

        setupToolbar()
        setupRecyclerView()
        setupSearchView()
        setupSortSpinner()
        setupSwipeRefresh()
        loadStaffByUnit()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        staffAdapter = StaffAdapter(emptyList(), lifecycleScope) { staff ->
            if (!UserManager.isStaff()) {
                Toast.makeText(this, "Bạn không có quyền xem chi tiết", Toast.LENGTH_SHORT).show()
                return@StaffAdapter
            }

            val intent = Intent(this, StaffDetailActivity::class.java).apply {
                putExtra("STAFF_ID", staff.id)
            }
            startActivity(intent)
        }

        binding.recyclerViewStaff.apply {
            layoutManager = LinearLayoutManager(this@StaffListActivity)
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
        val sortOptions = arrayOf("Tên A-Z", "Tên Z-A")
        val adapter = ArrayAdapter(this, R.layout.spinner_item, sortOptions)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerSort.adapter = adapter

        binding.spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentSortOrder = if (position == 0) SortOrder.ASCENDING else SortOrder.DESCENDING
                sortStaff()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            loadStaffByUnit()
        }
    }

    private fun loadStaffByUnit() {
        if (unitId == null) return

        binding.progressBar.visibility = View.VISIBLE
        binding.layoutEmpty.visibility = View.GONE
        binding.recyclerViewStaff.visibility = View.GONE

        lifecycleScope.launch {
            allStaff = DataProvider.getStaffByUnitId(unitId!!)
            filteredStaff = allStaff

            binding.progressBar.visibility = View.GONE

            if (filteredStaff.isEmpty()) {
                binding.layoutEmpty.visibility = View.VISIBLE
                binding.recyclerViewStaff.visibility = View.GONE
            } else {
                binding.layoutEmpty.visibility = View.GONE
                binding.recyclerViewStaff.visibility = View.VISIBLE
                staffAdapter.updateData(filteredStaff)
                sortStaff()
            }

            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun filterStaff(query: String) {
        filteredStaff = allStaff.filter { it.fullName.contains(query, ignoreCase = true) }
        staffAdapter.updateData(filteredStaff)

        if (filteredStaff.isEmpty()) {
            binding.layoutEmpty.visibility = View.VISIBLE
            binding.recyclerViewStaff.visibility = View.GONE
        } else {
            binding.layoutEmpty.visibility = View.GONE
            binding.recyclerViewStaff.visibility = View.VISIBLE
        }
    }

    private fun sortStaff() {
        filteredStaff = if (currentSortOrder == SortOrder.ASCENDING) {
            filteredStaff.sortedBy { it.fullName }
        } else {
            filteredStaff.sortedByDescending { it.fullName }
        }

        staffAdapter.updateData(filteredStaff)
    }

    enum class SortOrder {
        ASCENDING, DESCENDING
    }
}
