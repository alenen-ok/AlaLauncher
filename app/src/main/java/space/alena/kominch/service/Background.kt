package space.alena.kominch.service

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.squareup.picasso.MemoryPolicy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import space.alena.kominch.app.MyApp
import java.io.File
import java.io.FileOutputStream
import com.squareup.picasso.Picasso
import kotlinx.coroutines.withContext
import space.alena.kominch.R
import java.util.*
import java.util.concurrent.TimeUnit

class Background {
    companion object{
        val WALLPAPER_NAME = "wallpaper"
        val scope = CoroutineScope(Dispatchers.Default)

        @SuppressLint("SimpleDateFormat")
        fun addWallpaperWork(interval: Int) {
            val currentDate = Calendar.getInstance()
            val date = Calendar.getInstance()

            date.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
            while(date.before(currentDate)) {
                date.add(Calendar.MINUTE, interval)
            }
            val timeDiff = date.timeInMillis - currentDate.timeInMillis
            val periodicWork = PeriodicWorkRequestBuilder<BackgroundWorker>(
                    interval.toLong(), TimeUnit.MINUTES
            )
                    .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                    .build()
            WorkManager.getInstance(MyApp.appContext).enqueueUniquePeriodicWork(
                    "WallpaperWork",
                    ExistingPeriodicWorkPolicy.KEEP,
                    periodicWork
            )
        }
        suspend fun getWallpaper(source: String): BitmapDrawable =
                withContext(scope.coroutineContext) {
                    var bitmap: Bitmap? = null
                    try {
                        bitmap = Picasso.get()
                                .load(getDiscCacheFile(WALLPAPER_NAME))
                                .get()
                    } catch (_: Exception) { }

                    if (bitmap == null) {
                        try {
                            updateBackground(source).join()
                            return@withContext BitmapDrawable(
                                    MyApp.appContext.resources,
                                    Picasso.get()
                                            .load(getDiscCacheFile(WALLPAPER_NAME))
                                            .get()
                            )
                        } catch (_: Exception) {
                            return@withContext BitmapDrawable(
                                    MyApp.appContext.resources,
                                    Picasso.get()?.load(R.drawable.my_placeholder)?.get()
                            )
                        }
                    } else {
                        return@withContext BitmapDrawable(
                                MyApp.appContext.resources,
                                bitmap
                        )
                    }

                }

        fun updateBackground(source: String) = scope.launch {
            updateCache(
                    Picasso.get().load(source).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.my_placeholder).get()
            ).join()
        }
        private fun updateCache(bitmap: Bitmap?) = scope.launch {
            val file = getDiscCacheFile(WALLPAPER_NAME)
            val fOut = FileOutputStream(file)
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, fOut)
            fOut.close()
        }
        private fun getDiscCacheFile(fileName: String): File {
            val path = MyApp.appContext.getDir("wallpapers", Context.MODE_PRIVATE)
            return File(path, fileName)
        }

    }

}