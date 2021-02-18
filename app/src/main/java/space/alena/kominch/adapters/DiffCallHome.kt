package space.alena.kominch.adapters

import androidx.recyclerview.widget.DiffUtil
import space.alena.kominch.models.AppDetail

class DiffCallHome: DiffUtil.ItemCallback<AppDetail>() {
    override fun areItemsTheSame(oldItem: AppDetail, newItem: AppDetail): Boolean {
        return oldItem.label == newItem.label && oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: AppDetail, newItem: AppDetail): Boolean {
        return oldItem.label == newItem.label && oldItem.name == newItem.name
    }

}