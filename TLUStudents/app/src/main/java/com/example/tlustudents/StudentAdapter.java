package com.example.tlustudents;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private Student[] students;

    public StudentAdapter(Student[] students) {
        this.students = students;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = students[position];
        holder.imageViewAvatar.setImageResource(student.getAvatar());
        holder.textViewName.setText(student.getFullname());
        holder.textViewSid.setText(student.getSid());
    }

    @Override
    public int getItemCount() {
        return students.length;
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewAvatar;
        TextView textViewName;
        TextView textViewSid;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewAvatar = itemView.findViewById(R.id.imv_student_ava);
            textViewName = itemView.findViewById(R.id.txt_student_name);
            textViewSid = itemView.findViewById(R.id.txt_student_sid);
        }
    }
}
