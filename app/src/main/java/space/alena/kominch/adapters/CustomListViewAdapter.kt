package space.alena.kominch.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import space.alena.kominch.R
import space.alena.kominch.models.CustomListItem

class CustomListViewAdapter(context: Context?, activity_profile: Int) :
        ArrayAdapter<CustomListItem>(context, activity_profile){

    var items : ArrayList<CustomListItem> = ArrayList()

    init {
        items.add(CustomListItem(ContextCompat.getDrawable(context!!, R.drawable.ic_call_black_24dp),
                "+375 44 582-73-29", "Mobile"))
        items.add(CustomListItem(null,
                "+375 44 582-73-29", "Work"))
        items.add(CustomListItem(ContextCompat.getDrawable(context, R.drawable.ic_email_black_24dp),
                "alena_kominch@mail.ru", "Personal"))
        items.add(CustomListItem(null,
                "alena_work@mail.ru", "Work"))
        items.add(CustomListItem(ContextCompat.getDrawable(context, R.drawable.ic_place_black_24dp),
                "Минск, Оktyabrskaya-10", "Home"))
        items.add(CustomListItem(null,
                "Минск, Оktyabrskaya-10", "Work"))
        items.add(CustomListItem(ContextCompat.getDrawable(context, R.drawable.ic_person_black_24dp),
                "@aaaaaaaaaaaaaaaaaaaaaaa____aaa", "Instagram"))
        items.add(CustomListItem(null,
                "@alenakominch", "Gitlab"))



    }
    override fun getCount() = items.size
    override fun getItem(position: Int) = items[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var v = convertView
        if (v == null) {
            val vi = LayoutInflater.from(context)
            v = vi.inflate(R.layout.custom_list_item, null)
        }
        val item =getItem(position)
        if(item!=null){
            v!!.findViewById<ImageView>(R.id.custom_item_icon).setImageDrawable(item.icon)
            v!!.findViewById<TextView>(R.id.custom_item_text1).text = item.text1
            v!!.findViewById<TextView>(R.id.custom_item_text2).text = item.text2
        }
        return v!!
    }
}