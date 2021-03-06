package com.elkins.gamesradar.utility

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import java.io.File
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow


// Fragment extension for setting the title of the AppCompatActivity title bar
fun setSupportBarTitle(activity: Activity, title: String) {
    (activity as AppCompatActivity).supportActionBar?.title = title
}

/** Hide keyboard extension functions from
 *  https://dev.to/rohitjakhar/hide-keyboard-in-android-using-kotlin-in-20-second-18gp */
fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}


/**
 * Get and format cache size functions modified from stack overflow answer
 * @link https://stackoverflow.com/a/35488027/18063266
 */
fun FragmentActivity.getAppCacheSize(): String {
    val totalSize = initializeCache(this)
    return readableFileSize(totalSize)
}

private fun initializeCache(activity: FragmentActivity): Long {
    val internalCacheSize = getDirSize(activity.cacheDir)
    var externalCacheSize = 0L
    if(activity.externalCacheDir != null){
        externalCacheSize = getDirSize(activity.externalCacheDir!!)
    }
    return internalCacheSize + externalCacheSize
}

private fun getDirSize(dir: File): Long {
    var size: Long = 0
    for (file in dir.listFiles()!!) {
        if (file != null && file.isDirectory) {
            size += getDirSize(file)
        } else if (file != null && file.isFile) {
            size += file.length()
        }
    }
    return size
}

private fun readableFileSize(size: Long): String {
    if (size <= 0) return "0 Bytes"
    val units = arrayOf("Bytes", "kB", "MB", "GB", "TB")
    val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()

    return DecimalFormat("#,##0.#").format(size / 1024.0.pow(digitGroups.toDouble()))
        .toString() + " " + units[digitGroups]
}