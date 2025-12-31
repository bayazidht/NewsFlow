package com.bayazidht.newsflow.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import com.bayazidht.newsflow.R
import com.bayazidht.newsflow.databinding.FragmentCustomizeBinding
import com.google.android.material.chip.Chip
import androidx.core.net.toUri
import androidx.core.content.edit

class CustomizeFragment : Fragment(R.layout.fragment_customize) {

    private var _binding: FragmentCustomizeBinding? = null
    private val binding get() = _binding!!

    private val PREFS_NAME = "NewsPrefs"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCustomizeBinding.bind(view)

        loadSavedSettings()

        binding.btnAboutUs.setOnClickListener {
            showAboutDialog()
        }

        binding.btnPrivacyPolicy.setOnClickListener {
            val url = "https://rss.com/privacy-policy/"
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(requireContext(), url.toUri())
        }

        setupDarkMode()
        handleChipSelections()
    }

    private fun handleChipSelections() {
        val sharedPref = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        binding.chipGroupRegions.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedRegion = if (checkedIds.isNotEmpty()) {
                group.findViewById<Chip>(checkedIds.first()).text.toString()
            } else {
                "Global"
            }
            sharedPref.edit { putString("user_region", selectedRegion) }
            Log.d("CustomizeFragment", "Saved Region: $selectedRegion")
        }

        binding.chipGroupInterests.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedInterests = checkedIds.map { id ->
                group.findViewById<Chip>(id).text.toString()
            }.toSet()

            sharedPref.edit { putStringSet("selected_interests", selectedInterests) }
            Log.d("CustomizeFragment", "Saved Interests: $selectedInterests")
        }
    }

    private fun setupDarkMode() {
        val sharedPref = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPref.edit { putBoolean("dark_mode", true) }
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPref.edit { putBoolean("dark_mode", false) }
            }
        }
    }

    private fun loadSavedSettings() {
        val sharedPref = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val isDarkMode = sharedPref.getBoolean("dark_mode", false)
        binding.switchDarkMode.isChecked = isDarkMode

        val savedRegion = sharedPref.getString("user_region", "Global")
        for (i in 0 until binding.chipGroupRegions.childCount) {
            val chip = binding.chipGroupRegions.getChildAt(i) as Chip
            if (chip.text.toString() == savedRegion) {
                chip.isChecked = true
                break
            }
        }

        val savedInterests = sharedPref.getStringSet("selected_interests", setOf())
        for (i in 0 until binding.chipGroupInterests.childCount) {
            val chip = binding.chipGroupInterests.getChildAt(i) as Chip
            if (savedInterests?.contains(chip.text.toString()) == true) {
                chip.isChecked = true
            }
        }
    }

    private fun showAboutDialog() {
        val message = """
        NewsFlow is your ultimate destination for instant global and regional news.
        
        • Personalized News Feed
        • Real-time Trending Stories
        • Offline Bookmarks
        
        Developed by: Syed Bayazid Hossain
        Version: 1.0.0
    """.trimIndent()

        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle("About "+getString(R.string.app_name))
            .setIcon(R.mipmap.ic_launcher)
            .setMessage(message)
            .setPositiveButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}