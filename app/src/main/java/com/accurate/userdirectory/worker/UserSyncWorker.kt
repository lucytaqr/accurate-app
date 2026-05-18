package com.accurate.userdirectory.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.accurate.userdirectory.domain.usecase.SyncPendingUsersUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.concurrent.TimeUnit

@HiltWorker
class UserSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncPendingUsersUseCase: SyncPendingUsersUseCase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Timber.d("UserSyncWorker: Starting sync")
        return try {
            val result = syncPendingUsersUseCase()
            result.fold(
                onSuccess = { count ->
                    Timber.d("UserSyncWorker: Synced $count users")
                    Result.success()
                },
                onFailure = { e ->
                    Timber.e(e, "UserSyncWorker: Sync failed")
                    if (runAttemptCount < 3) Result.retry() else Result.failure()
                }
            )
        } catch (e: Exception) {
            Timber.e(e, "UserSyncWorker: Error syncing")
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "user_sync_worker"

        fun enqueue(context: Context) {
            val workRequest = OneTimeWorkRequestBuilder<UserSyncWorker>()
                .setInitialDelay(3, TimeUnit.SECONDS)
                .addTag(WORK_NAME)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, workRequest)
        }
    }
}
