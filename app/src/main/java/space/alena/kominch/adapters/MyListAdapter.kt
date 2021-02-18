package space.alena.kominch.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_launcher.view.*
import kotlinx.android.synthetic.main.item_list.view.*
import space.alena.kominch.R
import space.alena.kominch.models.AppDetail


class  MyListAdapter():  ListAdapter<Pair<AppDetail, Int>, RecyclerView.ViewHolder>(DiffCall()) {

    var typeOfLayout: Int = 1

    var onItemClick: (View?, Int) -> Unit = {_, _ -> }
    var onItemLongClick: (View?, Int) -> Boolean = { _, _ -> false}

    inner class LauncherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemLayoutSrc  = itemView.info_icon
        val itemLayoutName = itemView.info_text
        fun bind(itemApp: Pair<AppDetail, Int>, onItemClick: (View?, Int) -> Unit = {_, _ -> },
                 onItemLongClick: ((View?, Int) -> Boolean) = { _, _ -> false}) {
            itemLayoutName.text = itemApp.first.label
            itemLayoutSrc.setImageDrawable(itemApp.first.icon)
            itemView.setOnClickListener{onItemClick(it, layoutPosition)}
            itemView.setOnLongClickListener { onItemLongClick(it, layoutPosition) }
        }
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemLayoutSrc  = itemView.ic
        //val itemLayoutName = itemView.info_name
        val itemLayoutLabel = itemView.info_label
        fun bind(itemApp: Pair<AppDetail, Int>, onItemClick: (View?, Int) -> Unit = {_, _ -> },
                 onItemLongClick: ((View?, Int) -> Boolean) = { _, _ -> false}) {
            //itemLayoutName.text = itemApp.name
            itemLayoutLabel.text = itemApp.first.label
            itemLayoutSrc.setImageDrawable(itemApp.first.icon)
            itemView.setOnClickListener{onItemClick(it, layoutPosition)}
            itemView.setOnLongClickListener{onItemLongClick(it, layoutPosition)}
        }
    }

    override fun getItemViewType(position: Int): Int = typeOfLayout

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val obj: Pair<AppDetail, Int> = getItem(position)
        if(holder is LauncherViewHolder){
            (holder).bind(obj , onItemClick, onItemLongClick)
        }else {
            (holder as ListViewHolder).bind(obj , onItemClick, onItemLongClick)
        }
    }


    override fun submitList(list: MutableList<Pair<AppDetail, Int>>?) {
        super.submitList(list?.let { ArrayList(it) })
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if(viewType == 1){
            val view = layoutInflater.inflate(R.layout.item_launcher, parent, false)
            LauncherViewHolder(view)
        }else{
            val view = layoutInflater.inflate(R.layout.item_list, parent, false)
            ListViewHolder(view)
        }
    }


}