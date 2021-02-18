package space.alena.kominch.service

import android.app.IntentService
import android.content.Intent
import androidx.preference.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import space.alena.kominch.utils.Pref

class BackgroundLoaderService:IntentService("BackgroundLoaderService") {
    companion object{
        const val ACTION_DOWNLOAD_WALLPAPERS =
                "space.alena.kominch.DOWNLOAD_WALLPAPERS"
        const val BROADCAST_ACTION_UPDATE_WALLPAPERS =
                "space.alena.kominch.UPDATE_WALLPAPERS"
    }
    override fun onHandleIntent(intent: Intent?) {
        if(intent == null) return
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val source = prefs.getString(Pref.WALLPAPER_SOURCE, Pref.WallpapperSource.sourse1)
        when(intent.action){
            ACTION_DOWNLOAD_WALLPAPERS -> {
                CoroutineScope(Dispatchers.Default).launch {
                    try{
                        Background.updateBackground(source).join()
                        val broadcastIntent = Intent(BROADCAST_ACTION_UPDATE_WALLPAPERS)
                        sendBroadcast(broadcastIntent)
                    }catch(_: Exception){ }
                }
            }
        }
    }

}