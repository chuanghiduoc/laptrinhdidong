package com.baotrongit.tlucontact.ui.directory

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.baotrongit.tlucontact.R
import com.baotrongit.tlucontact.databinding.ActivityStudentDetailBinding
import com.baotrongit.tlucontact.data.model.Student
import com.baotrongit.tlucontact.utils.DataProvider
import com.baotrongit.tlucontact.utils.UserManager
import com.bumptech.glide.Glide

class StudentDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentDetailBinding
    private var student: Student? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val studentId = intent.getStringExtra("STUDENT_ID")
        if (studentId == null) {
            finish()
            return
        }

        loadStudentDetails(studentId)
        setupToolbar()
        setupActionButtons()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun loadStudentDetails(studentId: String) {
        student = DataProvider.getStudents().find { it.id == studentId }

        if (student == null) {
            finish()
            return
        }

        if (!canViewStudentDetails()) {
            Toast.makeText(this, "Bạn không có quyền xem thông tin này", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        binding.tvStudentId.text = "Mã sinh viên: ${student!!.studentCode}"
        binding.tvStudentName.text = student!!.fullName

        binding.layoutEmail.visibility = if (student!!.email != null) {
            binding.tvEmail.text = student!!.email
            View.VISIBLE
        } else {
            View.GONE
        }

        binding.layoutPhone.visibility = if (student!!.phone != null) {
            binding.tvPhone.text = student!!.phone
            View.VISIBLE
        } else {
            View.GONE
        }

        binding.layoutAddress.visibility = if (student!!.address != null) {
            binding.tvAddress.text = student!!.address
            View.VISIBLE
        } else {
            View.GONE
        }

        val unitName = DataProvider.getUnits().find { it.id == student!!.unitId }?.name ?: "Không xác định"
        binding.tvUnitName.text = unitName

        if (student!!.avatarUrl != null) {
            binding.ivStudentAvatar.clearColorFilter()
            Glide.with(this)
                .load(student!!.avatarUrl)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .into(binding.ivStudentAvatar)
        } else {
            binding.ivStudentAvatar.setImageResource(R.drawable.ic_person)
            binding.ivStudentAvatar.setColorFilter(ContextCompat.getColor(this, R.color.blue_500))
        }
    }

    private fun canViewStudentDetails(): Boolean {
        if (UserManager.isStaff()) {
            return true
        }

        val currentUserUnitId = UserManager.getCurrentUserUnitId()
        return currentUserUnitId != null && student?.unitId == currentUserUnitId
    }


    private fun setupActionButtons() {
        binding.btnCall.setOnClickListener {
            student?.phone?.let { phone ->
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$phone")
                }
                startActivity(intent)
            }
        }

        binding.btnEmail.setOnClickListener {
            student?.email?.let { email ->
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:$email")
                    putExtra(Intent.EXTRA_SUBJECT, "Liên hệ từ TLUContact")
                }
                startActivity(intent)
            }
        }

        binding.btnUnit.setOnClickListener {
            val intent = Intent(this, UnitDetailActivity::class.java).apply {
                putExtra("UNIT_ID", student?.unitId)
            }
            startActivity(intent)
        }
    }
}
