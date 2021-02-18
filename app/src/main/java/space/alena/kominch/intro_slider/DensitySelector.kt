package space.alena.kominch.intro_slider

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_density_selector.*
import space.alena.kominch.R
import space.alena.kominch.adapters.CustomAdapter
import space.alena.kominch.models.ItemContent


class DensitySelector : Fragment() {
    var orient: Int = RecyclerView.VERTICAL

    lateinit var adapter:CustomAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            orient = RecyclerView.HORIZONTAL
        return inflater.inflate(R.layout.fragment_density_selector, container, false)
    }

    override fun onStart() {
        var saveItems: ArrayList<ItemContent> = ArrayList()
        saveItems.add(ItemContent(resources.getString(R.string.slide4_standart),
                resources.getString(R.string.slide4_standart_descr)))
        saveItems.add(ItemContent(resources.getString(R.string.slide_4_heavy),
                resources.getString(R.string.slide4_heavy_descr)))

        adapter = CustomAdapter(saveItems)
        adapter.onItemClick=::onItemClick
        applyChanges()
        myRadioGroup.layoutManager = LinearLayoutManager(activity, orient, false)
        myRadioGroup.adapter = adapter
        super.onStart()
    }

     private fun applyChanges(){
         val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
         if(prefs.getBoolean(getString(R.string.id_layout_pref), false))
             adapter.selectedIndex = 1
         else
             adapter.selectedIndex = 0
         adapter.notifyDataSetChanged()
     }
    private fun onItemClick(view: View?, i: Int){
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        adapter.selectedIndex = i
        when(i) {
            0 -> prefs.edit().putBoolean(getString(R.string.id_layout_pref), false).apply()
            1 -> prefs.edit().putBoolean(getString(R.string.id_layout_pref), true).apply()
        }
        adapter.notifyDataSetChanged()
    }

}
