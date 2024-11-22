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

        renderThemeSettings(settingsViewModel.currentTheme.value)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingsViewModel.currentTheme.collect { currentTheme ->
                    renderThemeSettings(currentTheme)
                }
            }
        }

        binding.themeLL.setOnClickListener {
            settingsViewModel.changeTheme()
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
}