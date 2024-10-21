package com.example.ideacollector.notes.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ideacollector.R
import com.example.ideacollector.databinding.FragmentNotesBinding
import com.example.ideacollector.notes.domain.models.Note
import com.example.ideacollector.notes.presentation.models.NotesState
import com.example.ideacollector.notes.presentation.viewmodel.NotesViewModel
import com.example.ideacollector.util.getCurrentDateTime
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotesFragment : Fragment() {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    private val notesViewModel: NotesViewModel by viewModel()
    private val notesAdapter = NotesAdapter()
    private val iconDrawables = listOf(
        R.drawable.priority_red,
        R.drawable.priority_yellow,
        R.drawable.priority_green
    )

    private var currentIconIndex = 0

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

        updateNotes()

        binding.inputTextLayout.setEndIconOnClickListener {
            onSaveNoteClick()
        }

        binding.inputTextLayout.setEndIconOnLongClickListener {
            findNavController().navigate(
                R.id.action_notesFragment_to_settingsFragment
            )
            true
        }

        binding.inputTextLayout.setStartIconDrawable(iconDrawables[currentIconIndex])

        binding.inputTextLayout.setStartIconOnClickListener {
            currentIconIndex = (currentIconIndex + 1) % iconDrawables.size
            binding.inputTextLayout.setStartIconDrawable(iconDrawables[currentIconIndex])
        }

        binding.inputText.doOnTextChanged { text, _, _, _ ->
                binding.inputTextLayout.isEndIconCheckable = !text.isNullOrEmpty()
        }
    }

    private fun renderState(state: NotesState) {
        notesAdapter.notes.clear()
        notesAdapter.notifyDataSetChanged()
        when (state) {
            is NotesState.Empty -> showEmpty()
            is NotesState.Content -> showContent(state.notes)
        }
    }

    private fun updateNotes() {
        notesViewModel.getNotes()
        notesViewModel.observeState().observe(viewLifecycleOwner) {
            renderState(it)
        }
    }

    private fun showEmpty(): Unit = with(binding) {
        notesRecyclerView.isVisible = false
    }

    private fun showContent(notes: List<Note>) {
        binding.notesRecyclerView.isVisible = true

        notesAdapter.notes.addAll(notes)
        notesAdapter.notifyDataSetChanged()
    }

    private fun onSaveNoteClick() {
        val noteText = binding.inputText.text.toString()
        val noteData = getCurrentDateTime()
        notesViewModel.saveNote(currentIconIndex, noteText, noteData)
        binding.inputText.setText("")
        currentIconIndex = 0
        binding.inputTextLayout.setStartIconDrawable(iconDrawables[currentIconIndex])
        updateNotes()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}