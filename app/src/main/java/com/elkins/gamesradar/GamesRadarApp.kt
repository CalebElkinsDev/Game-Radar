package com.elkins.gamesradar

import android.app.Application
import androidx.work.*
import com.elkins.gamesradar.network.NetworkWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class GamesRadarApp : Application() {

    override fun onCreate() {
        super.onCreate()
        delayedInit()
    }

    private fun delayedInit() {
        CoroutineScope(Dispatchers.Default).launch {
            setupWork()
        }
    }

    private fun setupWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val repeatingRequest =
            PeriodicWorkRequestBuilder<NetworkWorker>(3, TimeUnit.DAYS)
                //.setConstraints(constraints)
//                .setInitialDelay(3, TimeUnit.DAYS)
                .setInitialDelay(5, TimeUnit.SECONDS)
                .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            NetworkWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            repeatingRequest
        )
    }
}