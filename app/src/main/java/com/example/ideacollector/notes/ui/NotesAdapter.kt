package com.example.ideacollector.notes.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ideacollector.databinding.NoteItemViewBinding
import com.example.ideacollector.notes.domain.models.Note

class NotesAdapter() : RecyclerView.Adapter<NotesViewHolder>(){
    var notes: ArrayList<Note> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {

        val layoutInspector = LayoutInflater.from(parent.context)
        return NotesViewHolder(
            NoteItemViewBinding.inflate(layoutInspector, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.bind(notes[position])
    }
}