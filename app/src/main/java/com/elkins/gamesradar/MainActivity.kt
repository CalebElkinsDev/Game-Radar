package com.elkins.gamesradar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import com.elkins.gamesradar.utility.PreferenceConstants

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setThemeFromPrefs()

        setContentView(R.layout.activity_main)

        // Enable "up" button
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    private fun setThemeFromPrefs() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)

        val mode = when (sharedPreferences.getString(PreferenceConstants.PREF_THEME, "MODE_NIGHT_FOLLOW_SYSTEM")) {
            "MODE_NIGHT_NO" -> AppCompatDelegate.MODE_NIGHT_NO
            "MODE_NIGHT_YES" -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        // Change the app's current theme to the mode from the user's preferences
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    /** Allow navigation to handle the "up" button */
    override fun onSupportNavigateUp(): Boolean {
        return Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp()
                || super.onSupportNavigateUp()
    }
}