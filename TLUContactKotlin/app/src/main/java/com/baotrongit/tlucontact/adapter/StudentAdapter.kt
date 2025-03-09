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
import com.baotrongit.tlucontact.data.model.Student
import com.baotrongit.tlucontact.utils.DataProvider
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class StudentAdapter(
    private var students: List<Student>,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val onItemClick: (Student) -> Unit
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    // Cache for unit names to avoid repeated Firestore calls
    private val unitNameCache = mutableMapOf<String, String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(students[position])
    }

    override fun getItemCount(): Int = students.size

    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivStudentAvatar: ImageView = itemView.findViewById(R.id.ivStudentAvatar)
        private val tvStudentName: TextView = itemView.findViewById(R.id.tvStudentName)
        private val tvStudentId: TextView = itemView.findViewById(R.id.tvStudentId)
        private val tvUnitName: TextView = itemView.findViewById(R.id.tvUnitName)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(students[position])
                }
            }
        }

        fun bind(student: Student) {
            tvStudentName.text = student.fullName
            tvStudentId.text = "Mã sinh viên: ${student.studentCode}"

            // Set default unit name initially
            tvUnitName.text = "Đang tải..."

            // Check if we have this unit name in our cache
            if (unitNameCache.containsKey(student.unitId)) {
                tvUnitName.text = unitNameCache[student.unitId]
            } else {
                // Fetch unit name from Firebase
                loadUnitName(student.unitId, tvUnitName)
            }

            // Display avatar if available
            if (student.avatarUrl != null) {
                Glide.with(itemView.context)
                    .load(student.avatarUrl)
                    .placeholder(R.drawable.ic_person) // Default image
                    .error(R.drawable.ic_person) // Image to display on error
                    .into(ivStudentAvatar)
            } else {
                // If no URL, use default image and apply tint
                ivStudentAvatar.setImageResource(R.drawable.ic_person)
                ivStudentAvatar.setColorFilter(ContextCompat.getColor(itemView.context, R.color.blue_500))
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

    fun updateData(newStudents: List<Student>) {
        students = newStudents
        notifyDataSetChanged()
    }
}
