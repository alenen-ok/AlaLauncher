package space.alena.kominch.app_fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.room.Room
import com.yandex.metrica.YandexMetrica
import kotlinx.android.synthetic.main.fragment_grid.*

import space.alena.kominch.R
import space.alena.kominch.activities.ApplicationListActivity
import space.alena.kominch.adapters.ChangeActivityManager
import space.alena.kominch.models.AppDetail
import space.alena.kominch.provider.AppDatabase

class GridFragment : Fragment(){

    private val adapter = ApplicationListActivity.adapter
    lateinit var contextActivity: Activity
    private lateinit var myDB: AppDatabase

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is Activity){
            contextActivity = context
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        myDB = Room.databaseBuilder(
                contextActivity.applicationContext,
                AppDatabase::class.java, "kominch.db"
        ).build()
        return inflater.inflate(R.layout.fragment_grid, container, false)
    }

    override fun onStart() {
        fab_plus.setOnClickListener {
            YandexMetrica.reportEvent("Click on fab")
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps"))
            startActivity(webIntent)
        }
        var typeOfLayout: Int = 4
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(contextActivity)
        if(prefs.getBoolean(getString(R.string.id_layout_pref), false))
            typeOfLayout++
        if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            typeOfLayout += 2
        }

        adapter.typeOfLayout = 1
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(activity, typeOfLayout)
        super.onStart()
    }


}
