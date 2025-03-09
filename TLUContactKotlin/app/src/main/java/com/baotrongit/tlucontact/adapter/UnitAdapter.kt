package com.baotrongit.tlucontact.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.baotrongit.tlucontact.R
import com.baotrongit.tlucontact.data.model.TLUUnit
import com.baotrongit.tlucontact.data.model.UnitType

class UnitAdapter(
    private var units: List<TLUUnit>,
    private val onItemClick: (TLUUnit) -> Unit
) : RecyclerView.Adapter<UnitAdapter.UnitViewHolder>() {

    private var filteredUnits: List<TLUUnit> = units

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_unit, parent, false)
        return UnitViewHolder(view)
    }

    override fun onBindViewHolder(holder: UnitViewHolder, position: Int) {
        holder.bind(filteredUnits[position])
    }

    override fun getItemCount(): Int = filteredUnits.size

    fun updateData(newUnits: List<TLUUnit>) {
        units = newUnits
        filteredUnits = newUnits
        notifyDataSetChanged()
    }

    fun filter(query: String, unitType: UnitType?) {
        filteredUnits = units.filter { unit ->
            val matchesQuery = if (query.isNotEmpty()) {
                unit.name.contains(query, ignoreCase = true) ||
                        (unit.code?.contains(query, ignoreCase = true) ?: false)
            } else true

            val matchesType = if (unitType != null) {
                unit.type == unitType
            } else true

            matchesQuery && matchesType
        }
        notifyDataSetChanged()
    }

    fun sortByName(ascending: Boolean) {
        filteredUnits = if (ascending) {
            filteredUnits.sortedBy { it.name }
        } else {
            filteredUnits.sortedByDescending { it.name }
        }
        notifyDataSetChanged()
    }

    inner class UnitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivUnitIcon: ImageView = itemView.findViewById(R.id.ivUnitIcon)
        private val tvUnitName: TextView = itemView.findViewById(R.id.tvUnitName)
        private val tvUnitCode: TextView = itemView.findViewById(R.id.tvUnitCode)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(filteredUnits[position])
                }
            }
        }

        fun bind(unit: TLUUnit) { // Thay đổi kiểu dữ liệu
            tvUnitName.text = unit.name

            if (unit.code != null) {
                tvUnitCode.visibility = View.VISIBLE
                tvUnitCode.text = "Mã đơn vị: ${unit.code}"
            } else {
                tvUnitCode.visibility = View.GONE
            }

            // Thiết lập biểu tượng dựa trên loại đơn vị
            val iconRes = when (unit.type) {
                UnitType.FACULTY -> R.drawable.ic_faculty
                UnitType.DEPARTMENT -> R.drawable.ic_department
                UnitType.OFFICE -> R.drawable.ic_office
                UnitType.CENTER -> R.drawable.ic_center
                UnitType.OTHER -> R.drawable.ic_unit
            }
            ivUnitIcon.setImageResource(iconRes)
        }
    }
}
