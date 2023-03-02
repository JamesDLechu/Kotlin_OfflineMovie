/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.android.devbyteviewer.work

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.android.devbyteviewer.database.getDatabase
import com.example.android.devbyteviewer.repository.VideosRepository
import com.example.android.devbyteviewer.util.makeStatusNotification
import retrofit2.HttpException
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class RefreshDataWorker(appContext: Context, params: WorkerParameters):
        CoroutineWorker(appContext, params){

    companion object {
        const val WORK_NAME= "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        Timber.i("Entra al worker")
        val database = getDatabase(applicationContext)
        val repository = VideosRepository(database)

        val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val actualDay = formatter.format(calendar.time)

        return try {
            repository.refreshVideos()
            makeStatusNotification("Actualizando datos: $actualDay", applicationContext)
            Result.success()
        } catch (e: HttpException) {
            Timber.i("Reintentando")
            Result.retry()
        }
    }

}