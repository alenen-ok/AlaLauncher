package space.alena.kominch.intro_slider

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.fragment_theme_selector.*
import space.alena.kominch.R
import space.alena.kominch.adapters.ChangeActivityManager


class ThemeSelector : Fragment() {
    lateinit var contextActivity: Activity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is Activity){
            contextActivity = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_theme_selector, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(contextActivity)
        if(prefs.getBoolean(getString(R.string.id_theme_pref), false))
            radioGroupTheme.check(R.id.radioButtonNight)
        else
            radioGroupTheme.check(R.id.radioButtonDay)
        radioGroupTheme.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
                    val radio: RadioButton = group.findViewById(checkedId)
                    when(radio.id){
                        R.id.radioButtonDay -> {
                            if(prefs.getBoolean(getString(R.string.id_theme_pref), false)) {
                                prefs.edit().putBoolean(getString(R.string.id_theme_pref), false).apply()
                                (activity as ChangeActivityManager).recreateWithPrefs()
                            }
                        }
                        R.id.radioButtonNight -> {
                            if(!prefs.getBoolean(getString(R.string.id_theme_pref), false)) {
                                prefs.edit().putBoolean(getString(R.string.id_theme_pref), true).apply()
                                (activity as ChangeActivityManager).recreateWithPrefs()
                            }
                        }
                    }
                })
        super.onViewCreated(view, savedInstanceState)
    }

}
