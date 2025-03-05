package com.example.tlucontact.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlucontact.DetailActivity;
import com.example.tlucontact.R;
import com.example.tlucontact.model.CBNV;

import java.util.List;

public class CBNVAdapter extends RecyclerView.Adapter<CBNVAdapter.CBNVViewHolder> {
    private List<CBNV> cbnvList;
    private String searchQuery = "";

    public CBNVAdapter(List<CBNV> cbnvList) {
        this.cbnvList = cbnvList;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery != null ? searchQuery.toLowerCase() : "";
    }

    @NonNull
    @Override
    public CBNVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cbnv, parent, false);
        return new CBNVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CBNVViewHolder holder, int position) {
        CBNV cbnv = cbnvList.get(position);

        // Áp dụng đánh dấu nếu truy vấn tìm kiếm không rỗng
        if (!TextUtils.isEmpty(searchQuery)) {
            // Đánh dấu tên
            highlightText(holder.textViewName, cbnv.getName(), searchQuery);

            // Đánh dấu chức vụ
            highlightText(holder.textViewPosition, cbnv.getPosition(), searchQuery);

            // Đánh dấu số điện thoại
            highlightText(holder.textViewPhone, cbnv.getPhone(), searchQuery);

            // Đánh dấu email
            highlightText(holder.textViewEmail, cbnv.getEmail(), searchQuery);
        } else {
            // Không có truy vấn tìm kiếm, chỉ hiển thị văn bản bình thường
            holder.textViewName.setText(cbnv.getName());
            holder.textViewPosition.setText(cbnv.getPosition());
            holder.textViewPhone.setText(cbnv.getPhone());
            holder.textViewEmail.setText(cbnv.getEmail());
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
            intent.putExtra("NAME", cbnv.getName());
            intent.putExtra("PHONE", cbnv.getPhone());
            intent.putExtra("EMAIL", cbnv.getEmail());
            intent.putExtra("ADDRESS", cbnv.getUnit());
            intent.putExtra("POSITION", cbnv.getPosition());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    private void highlightText(TextView textView, String fullText, String searchQuery) {
        if (TextUtils.isEmpty(searchQuery) || fullText == null) {
            textView.setText(fullText);
            return;
        }

        SpannableString spannableString = new SpannableString(fullText);
        String lowercaseFullText = fullText.toLowerCase();

        int startIndex = 0;
        while (startIndex != -1) {
            startIndex = lowercaseFullText.indexOf(searchQuery.toLowerCase(), startIndex);

            if (startIndex != -1) {
                int endIndex = startIndex + searchQuery.length();

                // Áp dụng kiểu chữ đậm
                spannableString.setSpan(new StyleSpan(Typeface.BOLD),
                        startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                // Áp dụng màu đánh dấu
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#1976D2")),
                        startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                startIndex = endIndex;
            }
        }

        textView.setText(spannableString);
    }

    @Override
    public int getItemCount() {
        return cbnvList.size();
    }

    static class CBNVViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewPosition;
        TextView textViewPhone;
        TextView textViewEmail;

        public CBNVViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.txt_cbnv_name);
            textViewPosition = itemView.findViewById(R.id.txt_cbnv_position);
            textViewPhone = itemView.findViewById(R.id.txt_cbnv_phone);
            textViewEmail = itemView.findViewById(R.id.txt_cbnv_email);
        }
    }

}
