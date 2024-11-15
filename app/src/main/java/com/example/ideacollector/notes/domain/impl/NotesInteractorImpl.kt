package com.example.ideacollector.notes.domain.impl

import com.example.ideacollector.notes.domain.api.NotesInteractor
import com.example.ideacollector.notes.domain.api.NotesRepository
import com.example.ideacollector.notes.domain.models.Note
import com.example.ideacollector.settings.domain.api.SettingsRepository
import com.example.ideacollector.settings.domain.models.SortType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class NotesInteractorImpl(
    private val notesRepository: NotesRepository,
    private val settingsRepository: SettingsRepository
) : NotesInteractor {

    override fun getAllNotes(): Flow<List<Note>> {
        return combine(
            notesRepository.getAllNotes(),
            settingsRepository.getSortType()
        ) { notes, sortType ->
            when(sortType) {
                // TODO здесь надо проверить сортировку - сейчас там строки и неверно будет сортироваться
                SortType.DATE -> notes.sortedBy { it.date }
                SortType.PRIORITY -> notes.sortedBy { it.priority }
            }
        }
    }

    override suspend fun addNewNote(note: Note) {
        return notesRepository.addNewNote(note)
    }

    override suspend fun deleteNote(id: Int) {
        notesRepository.deleteNote(id)
    }

    override suspend fun editNote(note: Note) {
        notesRepository.updateNote(note)
    }

}