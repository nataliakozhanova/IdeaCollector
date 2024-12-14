package com.example.ideacollector.notes.presentation.ui

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

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

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                notesViewModel.isScreenUnlocked.collect { isUnlocked ->
                    renderLockScreen(isUnlocked)
                }
            }
        }

        binding.passEnabledIV.setOnClickListener {
            showCheckPasswordDialog {
                notesViewModel.unlockScreen(true)
            }
        }

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
            notesViewModel.userClickedSaveButton(noteText)
            binding.inputText.setText("")
        }

        binding.inputTextLayout.setEndIconOnLongClickListener {
            if (notesViewModel.isScreenUnlocked.value) {
                navigateToSettings()
            } else {
                showCheckPasswordDialog {
                    notesViewModel.unlockScreen(true)
                    navigateToSettings()
                }
            }
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
        notesViewModel.userClickedDeleteNote(note)
    }

    private fun onEditNoteClick(oldNote: Note) {
        showEditNoteDialog(oldNote.text, oldNote.priority) { newText, newPriority ->
            notesViewModel.userClickedEditNote(oldNote, newText, newPriority)
        }
    }

    private fun showEditNoteDialog(
        initialText: String,
        initialPriority: String,
        onNoteSaved: (String, String) -> Unit,
    ) {
        notesViewModel.updateEditedPriority(Priority.valueOf(initialPriority))
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_note, null)
        val textInputLayout = dialogView.findViewById<TextInputLayout>(R.id.inputEditingTextLayout)
        val editText = dialogView.findViewById<TextInputEditText>(R.id.inputEditingText)
        editText.setTextCursorDrawable(R.drawable.custom_cursor_color)
        editText.setText(initialText)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                notesViewModel.editedPriority.collect { editedPriority ->
                    when (editedPriority) {
                        Priority.LOW -> textInputLayout.setStartIconDrawable(iconDrawables[2])
                        Priority.MEDIUM -> textInputLayout.setStartIconDrawable(iconDrawables[1])
                        Priority.HIGH -> textInputLayout.setStartIconDrawable(iconDrawables[0])
                    }
                }
            }
        }

        notesViewModel.setInitialPriority(Priority.valueOf(initialPriority))

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

    private fun renderLockScreen(isScreenUnlocked: Boolean) {
        with(binding) {
            notesRecyclerView.isVisible = isScreenUnlocked
            passEnabledView.isVisible = !isScreenUnlocked
            passEnabledIV.isVisible = !isScreenUnlocked
        }
    }

    private fun showCheckPasswordDialog(onPasswordSuccess: () -> Unit) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_check_password, null)
        val checkPasswordInputLayout =
            dialogView.findViewById<TextInputLayout>(R.id.checkPasswordInputLayout)
        val checkPasswordEditText = dialogView.findViewById<EditText>(R.id.checkPasswordEditText)

        val titleView = TextView(requireContext()).apply {
            text = getString(R.string.dialog_password_header)
            setTextAppearance(R.style.dialogHeaderPasswordText) // Применяем стиль
            setPadding(40, 40, 40, 40) // Устанавливаем отступы
            gravity = Gravity.CENTER // Центрируем текст
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setCustomTitle(titleView)
            .setView(dialogView)
            .setPositiveButton(R.string.dialog_ok_button, null)
            .setNegativeButton(R.string.dialog_cancel_button) { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
                val password = checkPasswordEditText.text?.toString()?.trim() ?: ""

                // Сбрасываем ошибки
                checkPasswordInputLayout.error = null

                // Не блокируем UI, пока ждём результат
                notesViewModel.checkPassword(password)

                viewLifecycleOwner.lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        notesViewModel.passwordCheckResult.collect { isPasswordCorrect ->
                            if (isPasswordCorrect == null) return@collect // Ждём результат

                            if (isPasswordCorrect) {
                                onPasswordSuccess() // Успех
                                notesViewModel.resetPasswordCheckResult()
                                dialog.dismiss()
                            } else {
                                checkPasswordInputLayout.error =
                                    getString(R.string.dialog_error_password_incorrect)
                            }
                        }
                    }
                }
            }

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
        }

        dialog.show()
    }

    private fun navigateToSettings() {
        findNavController().navigate(
            R.id.action_notesFragment_to_settingsFragment
        )
    }

    override fun onPause() {
        super.onPause()
        notesViewModel.unlockScreen(false) // Блокируем экран при сворачивании приложения
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}