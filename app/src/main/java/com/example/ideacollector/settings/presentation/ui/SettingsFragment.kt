package com.example.ideacollector.settings.presentation.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.ideacollector.R
import com.example.ideacollector.databinding.FragmentSettingsBinding
import com.example.ideacollector.settings.domain.models.SortType
import com.example.ideacollector.settings.domain.models.Theme
import com.example.ideacollector.settings.presentation.viewmodel.SettingsViewModel
import com.google.android.material.textfield.TextInputLayout
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

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingsViewModel.isPasswordEnabled.collect { enablePassword ->
                    binding.enablePasswordCheckbox.isChecked = enablePassword
                    binding.setPasswordLL.apply {
                        isClickable = enablePassword
                        alpha = if (enablePassword) 1f else 0.5f // Визуальный эффект для отключения
                    }
                    renderSetPasswordSettings(false)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingsViewModel.isPasswordSet.collect { isPasswordSet ->
                    renderSetPasswordSettings(isPasswordSet)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingsViewModel.sortTypeState.collect { sortType ->
                    renderSortTypeSettings(sortType)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingsViewModel.currentThemeSettings.collect { currentTheme ->
                    renderThemeSettings(currentTheme)
                }
            }
        }

        binding.enablePasswordCheckbox.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.changeCheckboxIsPasswordEnabled(isChecked)
        }

        binding.setPasswordLL.setOnClickListener {
            showSetPasswordDialog { password -> settingsViewModel.setPassword(password) }
        }

        binding.sortTypeLL.setOnClickListener {
            settingsViewModel.changeSortType()
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
            Theme.SYSTEM -> binding.themeSettingsTV.setText(R.string.theme_settings_system)
        }
    }

    private fun renderSortTypeSettings(sortType: SortType) {
        when (sortType) {
            SortType.PRIORITY -> binding.sortingSettingsTV.setText(R.string.sorting_settings_priority)
            SortType.DATE -> binding.sortingSettingsTV.setText(R.string.sorting_settings_date)
        }
    }

    private fun renderSetPasswordSettings(isPasswordSet: Boolean) = if (isPasswordSet) {
        binding.setPasswordSettingsTV.setText(R.string.password_settings_on)
    } else {
        binding.setPasswordSettingsTV.setText(R.string.password_settings_off)
    }

    private fun showSetPasswordDialog(onPasswordSet: (String) -> Unit) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_set_password, null)
        val passwordInputLayout = dialogView.findViewById<TextInputLayout>(R.id.passwordInputLayout)
        val confirmPasswordInputLayout =
            dialogView.findViewById<TextInputLayout>(R.id.confirmPasswordInputLayout)
        val passwordEditText = dialogView.findViewById<EditText>(R.id.passwordEditText)
        val confirmPasswordEditText =
            dialogView.findViewById<EditText>(R.id.confirmPasswordEditText)

        val titleView = TextView(requireContext()).apply {
            text = getString(R.string.password_header)
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
            // Устанавливаем кастомный цвет для кнопок
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
                val password = passwordEditText.text?.toString() ?: ""
                val confirmPassword = confirmPasswordEditText.text?.toString() ?: ""

                // Сбрасываем ошибки
                passwordInputLayout.error = null
                confirmPasswordInputLayout.error = null

                // Проверяем, чтобы поля не были пустыми
                if (password.isBlank()) {
                    passwordInputLayout.error = getString(R.string.dialog_error)
                    return@setOnClickListener
                }
                if (confirmPassword.isBlank()) {
                    confirmPasswordInputLayout.error = getString(R.string.dialog_error)
                    return@setOnClickListener
                }

                if (password != confirmPassword) {
                    confirmPasswordInputLayout.error =
                        getString(R.string.dialog_error_password_mismatch)
                    return@setOnClickListener
                }

                // Если проверки пройдены
                onPasswordSet(password)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

}