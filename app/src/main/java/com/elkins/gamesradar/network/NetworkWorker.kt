package com.elkins.gamesradar.network

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters


class NetworkWorker(context: Context, params: WorkerParameters)
    : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d("WorkManager", "Starting Work")
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "NetworkWorker"
    }
}