package space.alena.kominch.app_fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yandex.metrica.YandexMetrica
import kotlinx.android.synthetic.main.fragment_linear.*

import space.alena.kominch.R
import space.alena.kominch.activities.ApplicationListActivity
import space.alena.kominch.adapters.MyListAdapter


class LinearFragment : Fragment() {

    val adapter = ApplicationListActivity.adapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_linear, container, false)
    }

    override fun onStart() {
        YandexMetrica.reportEvent("Show linear mode")
        fab_plus.setOnClickListener {
            YandexMetrica.reportEvent("Click on fab")
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps"))
            startActivity(webIntent)
        }

        adapter.typeOfLayout = 2
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        super.onStart()
    }
}
