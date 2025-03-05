package com.baotrongit.tlucontact.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.baotrongit.tlucontact.R
import com.baotrongit.tlucontact.data.model.Student
import com.baotrongit.tlucontact.utils.DataProvider
import com.bumptech.glide.Glide

class StudentAdapter(
    private var students: List<Student>,
    private val onItemClick: (Student) -> Unit
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

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
            tvUnitName.text = DataProvider.getUnits().find { it.id == student.unitId }?.name ?: "Không xác định"

            // Hiển thị ảnh đại diện nếu có
            if (student.avatarUrl != null) {
                Glide.with(itemView.context)
                    .load(student.avatarUrl)
                    .placeholder(R.drawable.ic_person) // Hình mặc định
                    .error(R.drawable.ic_person) // Hình hiển thị khi lỗi
                    .into(ivStudentAvatar)
            } else {
                // Nếu không có URL, sử dụng hình mặc định và có thể áp dụng tint cho nó
                ivStudentAvatar.setImageResource(R.drawable.ic_person)
                ivStudentAvatar.setColorFilter(ContextCompat.getColor(itemView.context, R.color.blue_500))
            }
        }

    }

    fun updateData(newStudents: List<Student>) {
        students = newStudents
        notifyDataSetChanged()
    }
}
