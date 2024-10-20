package com.example.ideacollector.notes.ui

import androidx.recyclerview.widget.RecyclerView
import com.example.ideacollector.databinding.NoteItemViewBinding
import com.example.ideacollector.notes.domain.models.Note

class NotesViewHolder(private val binding: NoteItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Note) {
        //binding.priorityImageView.setImageResource(item.priority)
        binding.noteTextView.text = item.noteText
        binding.dataTextView.text = item.noteData
    }
}