package com.bayazidht.newsflow.ui.fragment

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

class CustomizeFragment : Fragment(R.layout.fragment_customize) {

    private var _binding: FragmentCustomizeBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCustomizeBinding.bind(view)

        binding.btnAboutUs.setOnClickListener {

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
        binding.chipGroupRegions.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedRegions = checkedIds.map { id ->
                group.findViewById<Chip>(id).text.toString()
            }

            Log.d("CustomizeFragment", "Selected Regions: $selectedRegions")

        }

        binding.chipGroupInterests.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedInterests = checkedIds.map { id ->
                group.findViewById<Chip>(id).text.toString()
            }

            Log.d("CustomizeFragment", "Selected Interests: $selectedInterests")

        }
    }

    private fun setupDarkMode() {
        binding.switchDarkMode.isChecked =
            AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}