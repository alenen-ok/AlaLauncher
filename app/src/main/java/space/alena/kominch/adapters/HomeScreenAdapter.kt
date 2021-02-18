package space.alena.kominch.adapters

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_launcher.view.*
import space.alena.kominch.R
import space.alena.kominch.models.AppDetail
import androidx.recyclerview.widget.ListAdapter

class HomeScreenAdapter(val context: Context) : ListAdapter<AppDetail, RecyclerView.ViewHolder>(DiffCallHome()),
        ItemTouchHelperAdapter {

    var onDeleteClick: (Int) -> Unit = { _ -> }
    var onItemClick: (View?, Int) -> Unit = {_, _ -> }
    val EMPTY_VIEW = 10
    val DELETE_VIEW = 11

    inner class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(){
        }
    }
    inner class DeleteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(appDetail: AppDetail) {
            itemView.info_text.text = appDetail.label
            itemView.info_icon.setImageDrawable(appDetail.icon)
        }
    }
    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(appDetail: AppDetail, onItemClick: (View?, Int) -> Unit = {_, _ -> }){
            itemView.info_icon.setImageDrawable(appDetail.icon)
            itemView.info_text.text = appDetail.label
            itemView.setOnClickListener{onItemClick(it, layoutPosition)}
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(currentList[position].label == ""){
            return EMPTY_VIEW
        }
        if(currentList[position].label == "Delete"){
            return DELETE_VIEW
        }
        return super.getItemViewType(position)
    }
    override fun submitList(list: MutableList<AppDetail>?) {
        super.submitList(list?.let { ArrayList(it) })
    }
    override fun onItemMove(fromPosition: Int, onPosition: Int) : Boolean{
        if(getItemViewType(onPosition) == EMPTY_VIEW){
            val items : ArrayList<AppDetail> = ArrayList()
            items.addAll(currentList)
            val app = currentList[fromPosition]
            items[fromPosition] = currentList[onPosition]
            items[onPosition] = app
            submitList(items)
            (context as ChangeHomeListener).refreshDataBase(app, onPosition)
        }
        if(getItemViewType(onPosition) == DELETE_VIEW){
            onDeleteClick(fromPosition)
            notifyDataSetChanged()
        }
        return false
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ItemViewHolder -> holder.bind(currentList[position], onItemClick)
            is DeleteViewHolder -> holder.bind(currentList[position])
            else -> (holder as EmptyViewHolder).bind()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_launcher, parent, false)
        return when(viewType) {
            EMPTY_VIEW -> EmptyViewHolder(view)
            DELETE_VIEW -> DeleteViewHolder(view)
            else ->  ItemViewHolder(view)
        }
    }
}