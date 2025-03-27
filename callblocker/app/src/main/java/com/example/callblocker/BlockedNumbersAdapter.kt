package com.example.callblocker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BlockedNumbersAdapter(
    private val blockedNumbers: List<String>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<BlockedNumbersAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val phoneNumberTextView: TextView = view.findViewById(R.id.phoneNumberTextView)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_blocked_number, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val number = blockedNumbers[position]
        holder.phoneNumberTextView.text = number
        holder.deleteButton.setOnClickListener {
            onDeleteClick(position)
        }
    }

    override fun getItemCount() = blockedNumbers.size
}
