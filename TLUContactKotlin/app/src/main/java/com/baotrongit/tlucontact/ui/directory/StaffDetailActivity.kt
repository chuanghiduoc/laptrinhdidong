package com.baotrongit.tlucontact.ui.directory

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.baotrongit.tlucontact.R
import com.baotrongit.tlucontact.databinding.ActivityStaffDetailBinding
import com.baotrongit.tlucontact.data.model.Staff
import com.baotrongit.tlucontact.utils.DataProvider
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class StaffDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStaffDetailBinding
    private var staff: Staff? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val staffId = intent.getStringExtra("STAFF_ID")
        if (staffId == null) {
            finish()
            return
        }

        setupToolbar()
        loadStaffDetails(staffId)
        setupActionButtons()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed();
        }
    }

    private fun loadStaffDetails(staffId: String) {
        // Use lifecycleScope to call the suspend function
        lifecycleScope.launch {
            try {
                // Fetch all staff and find the specific staff by ID
                val allStaff = DataProvider.getStaff()
                staff = allStaff.find { it.id == staffId }

                if (staff == null) {
                    finish()
                    return@launch
                }

                // Cập nhật UI với thông tin CBGV
                binding.tvStaffId.text = "Mã cán bộ: ${staff!!.staffId}"
                binding.tvStaffName.text = staff!!.fullName
                binding.tvPosition.text = staff!!.position

                // Thiết lập thông tin liên hệ
                if (staff!!.email != null) {
                    binding.layoutEmail.visibility = View.VISIBLE
                    binding.tvEmail.text = staff!!.email
                } else {
                    binding.layoutEmail.visibility = View.GONE
                }

                if (staff!!.phone != null) {
                    binding.layoutPhone.visibility = View.VISIBLE
                    binding.tvPhone.text = staff!!.phone
                } else {
                    binding.layoutPhone.visibility = View.GONE
                }

                // Thiết lập đơn vị trực thuộc
                binding.tvUnitName.text = "Đang tải..."
                try {
                    val unit = DataProvider.getUnitById(staff!!.unitId)
                    binding.tvUnitName.text = unit?.name ?: "Không xác định"
                } catch (e: Exception) {
                    binding.tvUnitName.text = "Không xác định"
                }

                // Hiển thị ảnh đại diện nếu có
                if (staff!!.avatarUrl != null) {
                    Glide.with(this@StaffDetailActivity) // Fix applied here
                        .load(staff!!.avatarUrl)
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person)
                        .into(binding.ivStaffAvatar)
                } else {
                    binding.ivStaffAvatar.setImageResource(R.drawable.ic_person)
                }
            } catch (e: Exception) {
                // Handle error
                finish()
            }
        }
    }

    private fun setupActionButtons() {
        // Thiết lập các nút hành động
        binding.btnCall.setOnClickListener {
            staff?.phone?.let { phone ->
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$phone")
                }
                startActivity(intent)
            }
        }

        binding.btnEmail.setOnClickListener {
            staff?.email?.let { email ->
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:$email")
                    putExtra(Intent.EXTRA_SUBJECT, "Liên hệ từ TLUContact")
                }
                startActivity(intent)
            }
        }


        binding.btnUnit.setOnClickListener {
            val intent = Intent(this, UnitDetailActivity::class.java).apply {
                putExtra("UNIT_ID", staff?.unitId)
            }
            startActivity(intent)
        }
    }
}
