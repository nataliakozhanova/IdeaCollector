package com.example.ideacollector.notes.ui

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ideacollector.R
import com.example.ideacollector.databinding.FragmentNotesBinding
import com.example.ideacollector.notes.domain.models.Note
import com.example.ideacollector.notes.domain.models.Priority
import com.example.ideacollector.notes.presentation.models.NotesState
import com.example.ideacollector.notes.presentation.viewmodel.NotesViewModel
import com.example.ideacollector.util.getCurrentDateTime
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

@Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
class NotesFragment : Fragment() {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    private val notesViewModel: NotesViewModel by viewModel()
    private val notesAdapter = NotesAdapter { note -> showNoteDialog(note) }
    private val iconDrawables = listOf(
        R.drawable.priority_red,
        R.drawable.priority_yellow,
        R.drawable.priority_green
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.notesRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        binding.inputText.setTextCursorDrawable(R.drawable.custom_cursor_color)
        binding.inputTextLayout.isEndIconCheckable = false

        binding.notesRecyclerView.adapter = notesAdapter

        observeNotes()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                notesViewModel.priority.collect { priority ->
                    when (priority) {
                        Priority.LOW -> binding.inputTextLayout.setStartIconDrawable(iconDrawables[2])
                        Priority.MEDIUM -> binding.inputTextLayout.setStartIconDrawable(
                            iconDrawables[1]
                        )

                        Priority.HIGH -> binding.inputTextLayout.setStartIconDrawable(iconDrawables[0])
                    }
                }
            }
        }

        binding.inputTextLayout.setEndIconOnClickListener {
            val noteText = binding.inputText.text.toString()
            val noteDate = getCurrentDateTime()
            notesViewModel.saveNoteIfValid(
                notesViewModel.priority.value.toString(),
                noteText,
                noteDate
            )
            binding.inputText.setText("")
        }

        binding.inputTextLayout.setEndIconOnLongClickListener {
            findNavController().navigate(
                R.id.action_notesFragment_to_settingsFragment
            )
            true
        }

        binding.inputTextLayout.setStartIconOnClickListener {
            notesViewModel.updatePriority()
        }

    }

    private fun renderState(state: NotesState) {
        when (state) {
            is NotesState.Empty -> showEmpty()
            is NotesState.Content -> showContent(state.notes)
        }
    }

    private fun observeNotes() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                notesViewModel.allNotes.collect {
                    renderState(it)
                }
            }
        }
    }

    private fun showEmpty(): Unit = with(binding) {
        notesRecyclerView.isVisible = false
    }

    private fun showContent(notes: List<Note>) {
        binding.notesRecyclerView.isVisible = true
        notesAdapter.submitList(notes.toList())
    }

    private fun onDeleteNoteClick(note: Note) {
        notesViewModel.deleteNote(note)
    }

    private fun onEditNoteClick(oldNote: Note) {
        showEditNoteDialog(oldNote.text, oldNote.priority) { newText, newPriority ->
            val newNote = Note(oldNote.id, newPriority, newText, getCurrentDateTime())
            notesViewModel.editNote(oldNote, newNote)
        }
    }

    private fun showEditNoteDialog(
        initialText: String,
        initialPriority: String,
        onNoteSaved: (String, String) -> Unit,
    ) {

        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_note, null)
        val textInputLayout = dialogView.findViewById<TextInputLayout>(R.id.inputEditingTextLayout)
        val editText = dialogView.findViewById<TextInputEditText>(R.id.inputEditingText)
        editText.setTextCursorDrawable(R.drawable.custom_cursor_color)
        editText.setText(initialText)

        notesViewModel.editedPriority.observe(viewLifecycleOwner) { editedPriority ->
            when (editedPriority) {
                Priority.LOW -> textInputLayout.setStartIconDrawable(iconDrawables[2])
                Priority.MEDIUM -> textInputLayout.setStartIconDrawable(iconDrawables[1])
                Priority.HIGH -> textInputLayout.setStartIconDrawable(iconDrawables[0])
            }
        }

        notesViewModel.setInitialPriority(enumValueOf<Priority>(initialPriority))

        textInputLayout.setStartIconOnClickListener {
            notesViewModel.editPriority()
        }

        // Отступ для текста ошибки
        textInputLayout.post {
            val errorTextView =
                textInputLayout.findViewById<TextView>(com.google.android.material.R.id.textinput_error)
            errorTextView?.setPadding(40, 0, 0, 0)
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_note_edit_item)
            .setView(dialogView)
            .setPositiveButton(R.string.dialog_save_button, null)
            .setNegativeButton(R.string.dialog_cancel_button) { dialog, _ ->
                dialog.dismiss()  // Закрываем диалог без сохранения
            }
            .show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
            val newText = editText.text.toString()
            val newPriority = notesViewModel.editedPriority.value.toString()
            if (newText.isNotBlank()) {
                onNoteSaved(newText, newPriority)  // Сохраняем только если текст не пустой
                dialog.dismiss()
            } else {
                textInputLayout.error =
                    getString(R.string.dialog_error)  // Устанавливаем текст ошибки, если текст пустой
            }
        }

        val colorOnPrimary = TypedValue()
        requireContext().theme.resolveAttribute(
            com.google.android.material.R.attr.colorOnPrimary,
            colorOnPrimary,
            true
        )
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(colorOnPrimary.data)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(colorOnPrimary.data)
    }


    private fun showNoteDialog(note: Note) {
        val items = arrayOf(
            getString(R.string.dialog_note_edit_item),
            getString(R.string.dialog_note_delete_item)
        )
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_note_header)
            .setItems(items) { _, which ->
                when (which) {
                    0 -> onEditNoteClick(note)
                    1 -> onDeleteNoteClick(note)
                }
            }
            .show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}