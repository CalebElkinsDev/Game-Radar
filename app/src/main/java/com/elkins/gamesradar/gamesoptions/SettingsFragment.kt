package com.elkins.gamesradar.gamesoptions

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.preference.MultiSelectListPreference
import androidx.preference.PreferenceFragmentCompat
import com.elkins.gamesradar.R
import com.elkins.gamesradar.gameslist.GamesListViewModel
import com.elkins.gamesradar.gameslist.GamesListViewModelFactory

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var viewModel: GamesListViewModel// by viewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        // Initialize ViewModel
        val viewModelFactory = GamesListViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(GamesListViewModel::class.java)

        //setPlatformsSummary()

        val pref = findPreference<MultiSelectListPreference>("platforms")
        pref?.setOnPreferenceChangeListener { preference, newValue ->

            var newList = mutableListOf<String>()

            for(item in (newValue as HashSet<String>)) {
                newList.add(item)
            }
            Log.d("Filter", newList.toString())
            viewModel.updateFilterPlatforms(newList)
            true
        }

        // TODO create listener for changes, update view model's DatabaseFilter on changes
//        viewModel.updateFilterPlatforms(listOf("MAC"))
    }



    /** Update the summary for the platforms section */
    private fun setPlatformsSummary() {
        val pref = findPreference<MultiSelectListPreference>("platforms")
        val selectedPlatforms = pref?.getPersistedStringSet(null)

        // Convert values to entries TODO change to extension function for other prefs
        val platformNames = selectedPlatforms?.map {
            val index = pref.findIndexOfValue(it)
            resources.getStringArray(R.array.list_platforms_full_entries)[index]
        }

        val selectedCount = platformNames?.size?: -1
        pref?.summary = when (selectedCount) {
            resources.getStringArray(R.array.list_platforms_full_entries).size -> {
                resources.getString(R.string.settings_platforms_all_selected)
            }
            in(1..3) -> {
                platformNames?.joinToString { it }
            }
            in(4..Integer.MAX_VALUE) -> {
                platformNames?.take(3)?.joinToString { it } + " and ${selectedCount - 3} more."
            }
            0 -> {
                resources.getString(R.string.settings_platforms_none_selected)
            }
            else -> {
                "Error"
            }
        }
    }
}