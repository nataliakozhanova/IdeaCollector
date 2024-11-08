package com.example.ideacollector.notes.ui

import androidx.recyclerview.widget.RecyclerView
import com.example.ideacollector.R
import com.example.ideacollector.databinding.NoteItemViewBinding
import com.example.ideacollector.notes.domain.models.Note
import com.example.ideacollector.notes.domain.models.Priority

class NotesViewHolder(
    private val onLongClickListener: NotesAdapter.NoteLongClickListener,
    private val binding: NoteItemViewBinding,
) : RecyclerView.ViewHolder(binding.root) {

    private val iconBackgroundDrawables = listOf(
        R.drawable.rectangle_red,
        R.drawable.rectangle_yellow,
        R.drawable.rectangle_green
    )

    fun bind(item: Note) {
        when (item.priority) {
            Priority.LOW.toString() -> binding.priorityImageView.setBackgroundResource(
                iconBackgroundDrawables[2]
            )

            Priority.MEDIUM.toString() -> binding.priorityImageView.setBackgroundResource(
                iconBackgroundDrawables[1]
            )

            Priority.HIGH.toString() -> binding.priorityImageView.setBackgroundResource(
                iconBackgroundDrawables[0]
            )
        }

        binding.noteTextView.text = item.text
        binding.dateTextView.text = item.date

        itemView.setOnLongClickListener {
            onLongClickListener.onNoteLongClick(item)
            true
        }
    }
}