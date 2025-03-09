package com.baotrongit.tlucontact.ui.directory

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.baotrongit.tlucontact.MainActivity
import com.baotrongit.tlucontact.R
import com.baotrongit.tlucontact.databinding.ActivityUnitDetailBinding
import com.baotrongit.tlucontact.data.model.TLUUnit
import com.baotrongit.tlucontact.utils.DataProvider
import com.baotrongit.tlucontact.utils.UserManager
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class UnitDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUnitDetailBinding
    private var unit: TLUUnit? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUnitDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val unitId = intent.getStringExtra("UNIT_ID")
        if (unitId == null) {
            finish()
            return
        }

        setupToolbar()
        loadUnitDetails(unitId)
        setupActionButtons()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed();
        }
    }

    private fun loadUnitDetails(unitId: String) {
        // Use lifecycleScope to call the suspend function
        lifecycleScope.launch {
            try {
                // Replace getUnits().find with direct getUnitById call
                unit = DataProvider.getUnitById(unitId)

                if (unit == null) {
                    finish()
                    return@launch
                }

                // Update UI with unit information
                updateUnitUI()
            } catch (e: Exception) {
                finish()
            }
        }
    }

    private fun updateUnitUI() {
        // Cập nhật UI với thông tin đơn vị
        binding.tvUnitName.text = unit!!.name
        binding.tvUnitType.text = unit!!.type.toString()

        // Hiển thị mã đơn vị
        if (unit!!.code != null) {
            binding.tvUnitCode.visibility = View.VISIBLE
            binding.tvUnitCode.text = "Mã đơn vị: ${unit!!.code}"
        } else {
            binding.tvUnitCode.visibility = View.GONE
        }
        // Cập nhật logo dựa trên link
        if (unit!!.logoUrl != null) {
            Glide.with(this)
                .load(unit!!.logoUrl)
                .placeholder(R.drawable.ic_unit)
                .error(R.drawable.ic_unit)
                .into(binding.ivUnitLogo)
        }

        // Thiết lập thông tin liên hệ
        if (unit!!.email != null) {
            binding.layoutEmail.visibility = View.VISIBLE
            binding.tvEmail.text = unit!!.email
        } else {
            binding.layoutEmail.visibility = View.GONE
        }

        if (unit!!.phone != null) {
            binding.layoutPhone.visibility = View.VISIBLE
            binding.tvPhone.text = unit!!.phone
        } else {
            binding.layoutPhone.visibility = View.GONE
        }

        if (unit!!.address != null) {
            binding.layoutAddress.visibility = View.VISIBLE
            binding.tvAddress.text = unit!!.address
        } else {
            binding.layoutAddress.visibility = View.GONE
        }

        if (unit!!.website != null) {
            binding.layoutWebsite.visibility = View.VISIBLE
            binding.tvWebsite.text = unit!!.website
        } else {
            binding.layoutWebsite.visibility = View.GONE
        }

        if (unit!!.description != null) {
            binding.cardDescription.visibility = View.VISIBLE
            binding.tvDescription.text = unit!!.description
        } else {
            binding.cardDescription.visibility = View.GONE
        }
    }

    private fun setupActionButtons() {
        binding.btnCall.setOnClickListener {
            unit?.phone?.let { phone ->
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$phone")
                }
                startActivity(intent)
            }
        }

        binding.btnEmail.setOnClickListener {
            unit?.email?.let { email ->
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:$email")
                    putExtra(Intent.EXTRA_SUBJECT, "Liên hệ từ TLUContact")
                }
                startActivity(intent)
            }
        }

        binding.btnWebsite.setOnClickListener {
            unit?.website?.let { website ->
                val url = if (!website.startsWith("http://") && !website.startsWith("https://")) {
                    "https://$website"
                } else {
                    website
                }
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(url)
                }
                startActivity(intent)
            }
        }

        binding.btnStaff.setOnClickListener {
            if (!UserManager.isStaff()) {
                Toast.makeText(this, "Bạn không có quyền xem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, StaffListActivity::class.java).apply {
                putExtra("UNIT_ID", unit?.id)
            }
            startActivity(intent)
        }

    }
}
