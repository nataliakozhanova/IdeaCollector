package com.example.ideacollector.notes.ui

import androidx.recyclerview.widget.RecyclerView
import com.example.ideacollector.R
import com.example.ideacollector.databinding.NoteItemViewBinding
import com.example.ideacollector.notes.domain.models.Note

class NotesViewHolder(private val binding: NoteItemViewBinding) : RecyclerView.ViewHolder(binding.root) {

    private val iconBackgroundDrawables = listOf(
        R.drawable.rectangle_red,
        R.drawable.rectangle_yellow,
        R.drawable.rectangle_green
    )
    fun bind(item: Note) {
        binding.priorityImageView.setBackgroundResource(iconBackgroundDrawables[item.priority])
        binding.noteTextView.text = item.noteText
        binding.dataTextView.text = item.noteData
    }
}