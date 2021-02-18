package space.alena.kominch.app

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import android.util.Log
import androidx.core.content.ContextCompat
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import com.yandex.metrica.push.YandexMetricaPush
import space.alena.kominch.BuildConfig

class MyApp : Application() {
    companion object {
        internal lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        val config = YandexMetricaConfig.newConfigBuilder("e117e835-2f07-4422-8b66-71705f509557")
        YandexMetrica.activate(applicationContext, config.withMaxReportsInDatabaseCount(1).build())
        YandexMetrica.enableActivityAutoTracking(this)
        YandexMetrica.reportEvent("Start this app")
        if (isInMainProcess()) YandexMetricaPush.init(applicationContext)

    }
    private fun isInMainProcess(): Boolean {
        val myPid = Process.myPid()
        val activityManager =
                ContextCompat.getSystemService(applicationContext, ActivityManager::class.java)
        return activityManager != null && activityManager.runningAppProcesses.any { process ->
            process.pid == myPid && process.processName == BuildConfig.APPLICATION_ID
        }
    }
}