package com.example.ideacollector.notes.presentation.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.ideacollector.databinding.NoteItemViewBinding
import com.example.ideacollector.notes.domain.models.Note

class NotesAdapter(private val noteLongClickListener: NoteLongClickListener) : ListAdapter<Note, NotesViewHolder>(
    DiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {

        val layoutInspector = LayoutInflater.from(parent.context)
        return NotesViewHolder(
            noteLongClickListener,
            NoteItemViewBinding.inflate(layoutInspector, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class DiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }

    fun interface NoteLongClickListener {
        fun onNoteLongClick(note: Note)
    }
}