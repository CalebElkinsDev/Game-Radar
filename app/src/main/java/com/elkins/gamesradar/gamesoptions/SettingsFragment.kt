package com.elkins.gamesradar.gamesoptions

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceFragmentCompat
import com.elkins.gamesradar.R
import com.elkins.gamesradar.gameslist.GamesListViewModel

class SettingsFragment : PreferenceFragmentCompat() {

    private val viewModel: GamesListViewModel by viewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}