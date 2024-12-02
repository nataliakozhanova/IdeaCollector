package com.example.ideacollector.notes.domain.impl

import com.example.ideacollector.notes.domain.api.NotesInteractor
import com.example.ideacollector.notes.domain.api.NotesRepository
import com.example.ideacollector.notes.domain.models.Note
import com.example.ideacollector.notes.domain.models.Priority
import com.example.ideacollector.settings.domain.api.SettingsRepository
import com.example.ideacollector.settings.domain.models.SortType
import com.example.ideacollector.util.parseDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDateTime

class NotesInteractorImpl(
    private val notesRepository: NotesRepository,
    private val settingsRepository: SettingsRepository,
) : NotesInteractor {

    override fun getAllNotes(): Flow<List<Note>> {
        return combine(
            notesRepository.getAllNotes(),
            settingsRepository.readSortingSettings()
        ) { notes, sortType ->
            when (sortType) {
                SortType.DATE -> notes.sortedByDescending {
                    parseDate(it.date) ?: LocalDateTime.MIN
                }

                SortType.PRIORITY -> notes.sortedByDescending {
                    runCatching { Priority.valueOf(it.priority).ordinal }.getOrElse { -1 }
                }

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

    override fun getEnablePassword(): Flow<Boolean> {
        return settingsRepository.readEnablePasswordSettings()
    }

    override suspend fun checkPassword(inputtedPassword: String): Flow<Boolean> {
        return settingsRepository.checkPassword(inputtedPassword)
    }
}