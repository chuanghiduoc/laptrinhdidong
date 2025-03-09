package com.baotrongit.tlucontact.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.baotrongit.tlucontact.R
import com.baotrongit.tlucontact.data.model.Staff
import com.baotrongit.tlucontact.utils.DataProvider
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class StaffAdapter(
    private var staffList: List<Staff>,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val onItemClick: (Staff) -> Unit
) : RecyclerView.Adapter<StaffAdapter.StaffViewHolder>() {

    // Cache for unit names to avoid repeated Firestore calls
    private val unitNameCache = mutableMapOf<String, String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_staff, parent, false)
        return StaffViewHolder(view)
    }

    override fun onBindViewHolder(holder: StaffViewHolder, position: Int) {
        holder.bind(staffList[position])
    }

    override fun getItemCount(): Int = staffList.size

    inner class StaffViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivStaffAvatar: ImageView = itemView.findViewById(R.id.ivStaffAvatar)
        private val tvStaffName: TextView = itemView.findViewById(R.id.tvStaffName)
        private val tvPosition: TextView = itemView.findViewById(R.id.tvPosition)
        private val tvUnitName: TextView = itemView.findViewById(R.id.tvUnitName)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(staffList[position])
                }
            }
        }

        fun bind(staff: Staff) {
            tvStaffName.text = staff.fullName
            tvPosition.text = staff.position

            // Set default unit name initially
            tvUnitName.text = "Đang tải..."

            // Check if we have this unit name in our cache
            if (unitNameCache.containsKey(staff.unitId)) {
                tvUnitName.text = unitNameCache[staff.unitId]
            } else {
                // Fetch unit name from Firebase
                loadUnitName(staff.unitId, tvUnitName)
            }

            // Display avatar if available
            if (staff.avatarUrl != null) {
                Glide.with(itemView.context)
                    .load(staff.avatarUrl)
                    .placeholder(R.drawable.ic_person) // Default image
                    .error(R.drawable.ic_person) // Image to display on error
                    .into(ivStaffAvatar)
            } else {
                ivStaffAvatar.setImageResource(R.drawable.ic_person)
                ivStaffAvatar.setColorFilter(ContextCompat.getColor(itemView.context, R.color.blue_500))
            }
        }
    }

    private fun loadUnitName(unitId: String, textView: TextView) {
        lifecycleScope.launch {
            try {
                val unit = DataProvider.getUnitById(unitId)
                val unitName = unit?.name ?: "Không xác định"

                // Update the cache
                unitNameCache[unitId] = unitName

                // Update the TextView
                textView.text = unitName
            } catch (e: Exception) {
                // Handle error
                textView.text = "Không xác định"
                // Still cache the result to avoid repeated failed calls
                unitNameCache[unitId] = "Không xác định"
            }
        }
    }

    fun updateData(newStaffList: List<Staff>) {
        staffList = newStaffList
        notifyDataSetChanged()
    }
}
