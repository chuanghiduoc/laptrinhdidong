package com.baotrongit.tlucontact.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.baotrongit.tlucontact.R
import com.baotrongit.tlucontact.data.model.Staff
import com.baotrongit.tlucontact.utils.DataProvider
import com.bumptech.glide.Glide

class StaffAdapter(
    private var staffList: List<Staff>,
    private val onItemClick: (Staff) -> Unit
) : RecyclerView.Adapter<StaffAdapter.StaffViewHolder>() {

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

            // Tìm đơn vị từ ID
            tvUnitName.text = DataProvider.getUnits().find { it.id == staff.unitId }?.name ?: "Không xác định"

            // Hiển thị ảnh đại diện nếu có
            if (staff.avatarUrl != null) {
                Glide.with(itemView.context)
                    .load(staff.avatarUrl)
                    .placeholder(R.drawable.ic_person) // Hình mặc định
                    .error(R.drawable.ic_person) // Hình hiển thị khi lỗi
                    .into(ivStaffAvatar)
            } else {
                ivStaffAvatar.setImageResource(R.drawable.ic_person)
                ivStaffAvatar.setColorFilter(ContextCompat.getColor(itemView.context, R.color.blue_500))
            }
        }
    }

    fun updateData(newStaffList: List<Staff>) {
        staffList = newStaffList
        notifyDataSetChanged()
    }
}
