package com.elkins.gamesradar.utility

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.elkins.gamesradar.database.DatabaseGame


// Fragment extension for setting the title of the AppCompatActivity title bar
fun Fragment.setSupportBarTitle(activity: Activity, title: String) {
    (activity as AppCompatActivity).supportActionBar?.title = title
}