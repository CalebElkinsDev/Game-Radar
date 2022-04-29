package com.elkins.gamesradar.gamesoptions

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.preference.ListPreference
import androidx.preference.MultiSelectListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.elkins.gamesradar.R
import com.elkins.gamesradar.gameslist.GamesListViewModel
import com.elkins.gamesradar.gameslist.GamesListViewModelFactory
import com.elkins.gamesradar.repository.GamesRepository
import com.elkins.gamesradar.utility.PreferenceConstants.Companion.PREF_PLATFORMS
import com.elkins.gamesradar.utility.PreferenceConstants.Companion.PREF_RELEASE_WINDOW
import com.elkins.gamesradar.utility.PreferenceConstants.Companion.PREF_SORT_ORDER

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var viewModel: GamesListViewModel// by viewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        // Initialize ViewModel
        val viewModelFactory = GamesListViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(GamesListViewModel::class.java)

        // Setup a onPreferenceChangeListener for the "Release Window" preference
        val releasesPref = findPreference<ListPreference>(PREF_RELEASE_WINDOW)
        releasesPref?.setOnPreferenceChangeListener { preference, newValue ->
            val newReleaseWindow = GamesRepository.ReleaseWindow.valueOf(newValue.toString().uppercase())

            // Notify the view model of a filter change
            viewModel.updateFilterReleaseDates(newReleaseWindow)

            true
        }

        // Setup a onPreferenceChangeListener for the "Platforms" preference
        val platformsPref = findPreference<MultiSelectListPreference>(PREF_PLATFORMS)
        platformsPref?.setOnPreferenceChangeListener { preference, newValue ->
            // Convert the preferences HashSet to an Array
            val newList = mutableListOf<String>()
            for(item in (newValue as HashSet<String>)) {
                newList.add(item)
            }
            // Notify the view model of a filter change
            viewModel.updateFilterPlatforms(newList)

            // Update the preferences summary
            setPlatformsSummary(newList)

            true
        }

        val sortOrderPref = findPreference<SwitchPreferenceCompat>(PREF_SORT_ORDER)
        sortOrderPref?.setOnPreferenceChangeListener { preference, newValue ->

            viewModel.updateFilterSortOrder(newValue as Boolean)
            Log.d("Switch", newValue.toString())

            true
        }

        setPlatformsSummary()
    }


    /** Update the summary for the platforms section based on the existing preferences */
    private fun setPlatformsSummary() {
        val pref = findPreference<MultiSelectListPreference>("platforms")
        val selectedPlatforms = pref?.getPersistedStringSet(null)

        // Get entries from values
        val platformNames = selectedPlatforms?.map {
            val index = pref.findIndexOfValue(it)
            resources.getStringArray(R.array.list_platforms_full_entries)[index]
        }

        pref?.summaryFromList(platformNames?: emptyList(), resources)
    }

    /**
     * Used with onPreferenceChangeListener to update with the new value since it triggers
     * before changes are applied.
     */
    private fun setPlatformsSummary(values: List<String>?) {
        val pref = findPreference<MultiSelectListPreference>("platforms")

        // Get entries from values
        val platformNames = values?.map {
            val index = pref?.findIndexOfValue(it)
            resources.getStringArray(R.array.list_platforms_full_entries)[index!!]
        }

        pref?.summaryFromList(platformNames?: emptyList(), resources)
    }
}

/**
 * Extension function to show a summary for MultiSelectListPreferences based on the number of
 * entries currently selected.
 */
fun MultiSelectListPreference.summaryFromList(values: List<String>, resources: Resources) {
    val selectedCount = values.size
    this.summary = when (selectedCount) {
        resources.getStringArray(R.array.list_platforms_full_entries).size -> {
            resources.getString(R.string.settings_all_selected)
        }
        in(1..3) -> {
            values.joinToString { it }
        }
        in(4..Integer.MAX_VALUE) -> {
            values.take(3).joinToString { it } + " and ${selectedCount - 3} more."
        }
        0 -> {
            resources.getString(R.string.settings_none_selected)
        }
        else -> {
            "Error: Should not ever occur"
        }
    }
}