package com.elkins.gamesradar.network

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.elkins.gamesradar.GamesRadarApp
import java.lang.Exception


/** CourotineWorker implementation for fetching and updating the database contents periodically. */
class NetworkWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        return try {
            GamesRadarApp.repository.getGamesFromNetwork()
            Result.success()

        } catch (e: Exception) {
            Log.e("Work Error", e.printStackTrace().toString())
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "NetworkWorker"
    }
}