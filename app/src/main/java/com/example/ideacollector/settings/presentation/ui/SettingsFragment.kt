package com.example.ideacollector.settings.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.ideacollector.R
import com.example.ideacollector.databinding.FragmentSettingsBinding
import com.example.ideacollector.settings.domain.models.SortType
import com.example.ideacollector.settings.domain.models.Theme
import com.example.ideacollector.settings.presentation.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private val settingsViewModel: SettingsViewModel by viewModel()
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        renderThemeSettings(settingsViewModel.currentThemeSettings.value)

        viewLifecycleOwner.lifecycleScope.launch {
            settingsViewModel.getSortType()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingsViewModel.currentThemeSettings.collect { currentTheme ->
                    renderThemeSettings(currentTheme)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingsViewModel.currentSortingSettings.collect { currentSortType ->
                    renderSortTypeSettings(currentSortType)
                }
            }
        }

        binding.themeLL.setOnClickListener {
            settingsViewModel.changeTheme()
        }

        binding.sortTypeLL.setOnClickListener {
            settingsViewModel.changeSortType()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun renderThemeSettings(theme: Theme) {
        when (theme) {
            Theme.LIGHT -> binding.themeSettingsTV.setText(R.string.theme_settings_light)
            Theme.DARK -> binding.themeSettingsTV.setText(R.string.theme_settings_dark)
        }
    }

    private fun renderSortTypeSettings(sortType: SortType) {
        when (sortType) {
            SortType.PRIORITY -> binding.sortingSettingsTV.setText(R.string.sorting_settings_priority)
            SortType.DATE -> binding.sortingSettingsTV.setText(R.string.sorting_settings_date)
        }
    }
}