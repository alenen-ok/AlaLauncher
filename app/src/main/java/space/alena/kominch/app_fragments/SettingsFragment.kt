package space.alena.kominch.app_fragments

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.yandex.metrica.YandexMetrica
import space.alena.kominch.R
import space.alena.kominch.adapters.ChangeActivityManager
import space.alena.kominch.utils.Pref
import space.alena.kominch.service.BackgroundLoaderService


class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener{
    private var settingView: View? = null
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        YandexMetrica.reportEvent("Go to settings")
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferenceManager.setDefaultValues(activity, R.xml.preferences, false);
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        settingView = super.onCreateView(inflater, container, savedInstanceState)
        settingView!!.setBackgroundColor(Color.TRANSPARENT)
        settingView!!.setBackgroundColor(ContextCompat.getColor(activity!!.baseContext.applicationContext, R.color.back_alpha_80))
        preferenceScreen.findPreference<Preference>(getString(R.string.id_refresh_now_pref))?.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    if (settingView != null) {
                        val prefs = PreferenceManager.getDefaultSharedPreferences(settingView?.context)
                        val value = prefs.getString(Pref.WALLPAPER_SOURCE, Pref.WallpapperSource.sourse1)
                        val intent = Intent(activity, BackgroundLoaderService::class.java)
                                .setAction(BackgroundLoaderService.ACTION_DOWNLOAD_WALLPAPERS)
                        settingView?.context?.startService(intent)
                        val snackbar = Snackbar.make(settingView!!, "Wallpaper updated",
                                Snackbar.LENGTH_LONG)
                        snackbar.show()
                    } else {
                        val snackbar = Snackbar.make(settingView!!, "Wallpaper isn't updated",
                                Snackbar.LENGTH_LONG)
                        snackbar.show()
                    }
                    true
                }
        return settingView
    }
    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences
                .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences
                .unregisterOnSharedPreferenceChangeListener(this);
    }
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if(key == getString(R.string.id_theme_pref)) {
            if(sharedPreferences!!.getBoolean(key, false))
                YandexMetrica.reportEvent("Set dark theme")
            (activity as ChangeActivityManager).recreateWithPrefs()
        }
        if(key == getString(R.string.id_type_of_sort)) {
            val eventParameters: HashMap<String, Any> = HashMap()
            eventParameters["Sort apps by"] = sharedPreferences!!.getString(key, "")
            YandexMetrica.reportEvent("Sort feature", eventParameters)
            (activity as ChangeActivityManager).applySortForAdapter()
        }
        if(key ==  getString(R.string.id_openmode_pref) && sharedPreferences!!.getBoolean(key, false)) {
            YandexMetrica.reportEvent("Try restart WelcomePage")
        }
        if(key ==  getString(R.string.id_layout_pref)){
            val eventParameters: HashMap<String, Any> = HashMap()
            eventParameters["Dense layout"] = sharedPreferences!!.getBoolean(key, false)
            YandexMetrica.reportEvent("Use layout-feature", eventParameters)
        }
    }
}
