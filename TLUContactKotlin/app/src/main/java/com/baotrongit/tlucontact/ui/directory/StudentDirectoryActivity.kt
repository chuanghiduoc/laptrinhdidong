package com.baotrongit.tlucontact.ui.directory

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.baotrongit.tlucontact.R
import com.baotrongit.tlucontact.adapter.StudentAdapter
import com.baotrongit.tlucontact.databinding.ActivityStudentDirectoryBinding
import com.baotrongit.tlucontact.data.model.Student
import com.baotrongit.tlucontact.data.model.UnitType
import com.baotrongit.tlucontact.utils.DataProvider
import com.baotrongit.tlucontact.utils.UserManager
import kotlinx.coroutines.launch

class StudentDirectoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentDirectoryBinding
    private lateinit var studentAdapter: StudentAdapter
    private var allStudents: List<Student> = emptyList()
    private var filteredStudents: List<Student> = emptyList()
    private var currentSortOrder = SortOrder.ASCENDING
    private var currentUnitType: UnitType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentDirectoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupSearchView()
        setupSortSpinner()
        setupFilterSpinner()
        setupSwipeRefresh()
        setupPermissionMessage()

        loadStudents()
    }

    private fun setupPermissionMessage() {
        if (UserManager.isStudent()) {
            binding.tvPermissionMessage.visibility = View.VISIBLE
            binding.tvPermissionMessage.text = "Bạn chỉ có thể xem thông tin sinh viên cùng đơn vị"
        } else {
            binding.tvPermissionMessage.visibility = View.GONE
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        // Pass lifecycleScope to the StudentAdapter
        studentAdapter = StudentAdapter(
            emptyList(),
            lifecycleScope
        ) { student ->
            val intent = Intent(this, StudentDetailActivity::class.java).apply {
                putExtra("STUDENT_ID", student.id)
            }
            startActivity(intent)
        }

        binding.rvStudents.apply {
            layoutManager = LinearLayoutManager(this@StudentDirectoryActivity)
            adapter = studentAdapter
        }
    }

    private fun setupSearchView() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filterStudents(s.toString())
            }
        })

        binding.ivClearSearch.setOnClickListener {
            binding.etSearch.text.clear()
        }
    }

    private fun setupSortSpinner() {
        val sortOptions = arrayOf("Tên A-Z", "Tên Z-A", "Msv A-Z", "Msv Z-A")
        val adapter = ArrayAdapter(this, R.layout.spinner_item, sortOptions)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerSort.adapter = adapter

        binding.spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentSortOrder = if (position == 0 || position == 1) {
                    SortOrder.ASCENDING
                } else {
                    SortOrder.DESCENDING
                }
                sortStudents(position)
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
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentUnitType = when (position) {
                    0 -> null
                    1 -> UnitType.FACULTY
                    2 -> UnitType.DEPARTMENT
                    3 -> UnitType.OFFICE
                    4 -> UnitType.CENTER
                    5 -> UnitType.OTHER
                    else -> null
                }
                filterStudents(binding.etSearch.text.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(R.color.blue_500)
        binding.swipeRefresh.setOnRefreshListener {
            loadStudents()
        }
    }

    private fun loadStudents() {
        binding.progressBar.visibility = View.VISIBLE
        binding.layoutEmpty.visibility = View.GONE
        binding.rvStudents.visibility = View.GONE

        // Use lifecycleScope to call the suspend function
        lifecycleScope.launch {
            allStudents = DataProvider.getStudents()

            val isStaff = UserManager.isStaff()
            val currentUserUnitId = UserManager.getCurrentUserUnitId()

            filteredStudents = if (isStaff) {
                allStudents
            } else {
                if (currentUserUnitId != null) {
                    val filtered = allStudents.filter { it.unitId == currentUserUnitId }
                    filtered
                } else {
                    emptyList()
                }
            }

            binding.progressBar.visibility = View.GONE

            if (filteredStudents.isEmpty()) {
                binding.layoutEmpty.visibility = View.VISIBLE
                binding.rvStudents.visibility = View.GONE
            } else {
                binding.layoutEmpty.visibility = View.GONE
                binding.rvStudents.visibility = View.VISIBLE
                studentAdapter.updateData(filteredStudents)
                sortStudents(0)
            }

            binding.swipeRefresh.isRefreshing = false
        }
    }


    private fun filterStudents(query: String) {
        filteredStudents = filteredStudents.filter { student ->
            student.fullName.contains(query, ignoreCase = true) ||
                    student.studentCode.contains(query, ignoreCase = true) ||
                    student.unitId.contains(query, ignoreCase = true)
        }

        studentAdapter.updateData(filteredStudents)

        if (studentAdapter.itemCount == 0) {
            binding.layoutEmpty.visibility = View.VISIBLE
            binding.rvStudents.visibility = View.GONE
        } else {
            binding.layoutEmpty.visibility = View.GONE
            binding.rvStudents.visibility = View.VISIBLE
        }
    }

    private fun sortStudents(sortOption: Int) {
        filteredStudents = when (sortOption) {
            0 -> filteredStudents.sortedBy { it.fullName } // Name A-Z
            1 -> filteredStudents.sortedByDescending { it.fullName } // Name Z-A
            2 -> filteredStudents.sortedBy { it.studentCode } // Student ID A-Z
            3 -> filteredStudents.sortedByDescending { it.studentCode } // Student ID Z-A
            else -> filteredStudents
        }

        studentAdapter.updateData(filteredStudents)
    }

    enum class SortOrder {
        ASCENDING, DESCENDING
    }
}
