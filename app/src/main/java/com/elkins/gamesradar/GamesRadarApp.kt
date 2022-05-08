package com.elkins.gamesradar

import android.app.Application
import androidx.work.*
import com.elkins.gamesradar.network.NetworkWorker
import com.elkins.gamesradar.repository.GamesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

private const val BACK_OFF_TIME = 60_000L

class GamesRadarApp : Application() {

    override fun onCreate() {
        super.onCreate()
        delayedInit()
    }

    private fun delayedInit() {

        REPOSITORY = GamesRepository(this)

        CoroutineScope(Dispatchers.Default).launch {
            setupWork()
        }
    }

    private fun setupWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresStorageNotLow(true)
            .build()

        val repeatingRequest =
            PeriodicWorkRequestBuilder<NetworkWorker>(3, TimeUnit.DAYS) //, 1, TimeUnit.HOURS)
                //.setConstraints(constraints)
//                .setInitialDelay(3, TimeUnit.DAYS)
                .setInitialDelay(5, TimeUnit.SECONDS)
//                .setBackoffCriteria(
//                    BackoffPolicy.LINEAR,
//                    BACK_OFF_TIME,
//                    TimeUnit.MILLISECONDS)
                .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            NetworkWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            repeatingRequest
        )
    }

    companion object {
        lateinit var REPOSITORY: GamesRepository
    }
}