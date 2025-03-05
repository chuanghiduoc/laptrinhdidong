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
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlucontact.DetailActivity;
import com.example.tlucontact.R;
import com.example.tlucontact.model.Unit;

import java.util.List;

public class UnitAdapter extends RecyclerView.Adapter<UnitAdapter.UnitViewHolder> {
    private List<Unit> unitList;
    private String searchQuery = "";

    public UnitAdapter(List<Unit> unitList) {
        this.unitList = unitList;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery != null ? searchQuery.toLowerCase() : "";
    }

    @NonNull
    @Override
    public UnitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_unit, parent, false);
        return new UnitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UnitViewHolder holder, int position) {
        Unit unit = unitList.get(position);

        // Áp dụng đánh dấu nếu truy vấn tìm kiếm không rỗng
        if (!TextUtils.isEmpty(searchQuery)) {
            // Đánh dấu tên
            highlightText(holder.textViewName, unit.getName(), searchQuery);

            // Đánh dấu số điện thoại
            highlightText(holder.textViewPhone, unit.getPhone(), searchQuery);

            // Đánh dấu địa chỉ
            highlightText(holder.textViewAddress, unit.getAddress(), searchQuery);
        } else {
            // Không có truy vấn tìm kiếm, chỉ hiển thị văn bản bình thường
            holder.textViewName.setText(unit.getName());
            holder.textViewPhone.setText(unit.getPhone());
            holder.textViewAddress.setText(unit.getAddress());
        }

        holder.iconUnit.setImageResource(R.drawable.ic_unit_icon);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
            intent.putExtra("NAME", unit.getName());
            intent.putExtra("PHONE", unit.getPhone());
            intent.putExtra("ADDRESS", unit.getAddress());
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
        return unitList.size();
    }

    static class UnitViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewPhone;
        TextView textViewAddress;
        ImageView iconUnit;

        public UnitViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.txt_unit_name);
            textViewPhone = itemView.findViewById(R.id.txt_unit_phone);
            textViewAddress = itemView.findViewById(R.id.txt_unit_address);
            iconUnit = itemView.findViewById(R.id.icon_unit);
        }
    }

}
