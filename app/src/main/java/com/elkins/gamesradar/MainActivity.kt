package com.elkins.gamesradar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.preference.MultiSelectListPreference
import androidx.preference.PreferenceManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO Remove when preferences are finished. Used for clearing edits
//        PreferenceManager.getDefaultSharedPreferences(baseContext).edit().clear().apply()
//        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, true)

        setContentView(R.layout.activity_main)

        // Enable "up" button
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    /** Allow navigation to handle the "up" button */
    override fun onSupportNavigateUp(): Boolean {
        return Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp()
                || super.onSupportNavigateUp()
    }
}