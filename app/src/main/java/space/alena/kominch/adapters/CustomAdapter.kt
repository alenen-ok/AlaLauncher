package space.alena.kominch.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.custom_radibutton.view.*
import space.alena.kominch.R
import space.alena.kominch.models.ItemContent

class CustomAdapter(var saveItems: List<ItemContent>)
                    : RecyclerView.Adapter<CustomAdapter.MyViewHolder>() {

    var selectedIndex = -1
    var onItemClick: (View?, Int) -> Unit = {_, _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_radibutton, parent, false)
        return MyViewHolder(view)
    }
    override fun getItemCount(): Int {
        return saveItems.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(saveItems[position])
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rbSelect = itemView.findViewById(R.id.myRadiobutton) as RadioButton
        fun bind(itemContent: ItemContent) {
            itemView.setOnClickListener{onItemClick(it, layoutPosition)}
            itemView.buttonName.text = itemContent.name
            itemView.buttonDescription.text = itemContent.description
            rbSelect.isChecked = selectedIndex == adapterPosition
        }
    }
}