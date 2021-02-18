package space.alena.kominch.service

import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters

class BackgroundWorker(val context: Context, params: WorkerParameters): Worker(context, params) {
    override fun doWork(): Result {
        val intent = Intent (context, BackgroundLoaderService::class.java)
                .setAction(BackgroundLoaderService.ACTION_DOWNLOAD_WALLPAPERS)
        context.startService(intent)
        return Result.success()
    }

}