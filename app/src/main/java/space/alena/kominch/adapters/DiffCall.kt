package space.alena.kominch.adapters

import androidx.recyclerview.widget.DiffUtil
import space.alena.kominch.models.AppDetail

class DiffCall : DiffUtil.ItemCallback<Pair<AppDetail, Int>>(){
    override fun areItemsTheSame(oldItem: Pair<AppDetail,Int>, newItem: Pair<AppDetail,Int>): Boolean {
        return oldItem.first.label == newItem.first.label && oldItem.first.name == newItem.first.name
    }

    override fun areContentsTheSame(oldItem: Pair<AppDetail,Int>, newItem: Pair<AppDetail,Int>): Boolean {
        return oldItem.first.label == newItem.first.label && oldItem.first.name == newItem.first.name
    }

}
