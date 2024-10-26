package com.fdhasna21.fond

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fdhasna21.fond.databinding.ItemRestoBinding
import com.fdhasna21.fond.model.response.Categories
import com.fdhasna21.fond.model.response.ItemResto
import com.fdhasna21.fond.utility.Utils

/**
 * Created by Fernanda Hasna on 26/10/2024.
 */

class ItemRestoAdapter(
    val context: Context,
    var data: ArrayList<ItemResto>
) : RecyclerView.Adapter<ItemRestoAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemRestoBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRestoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        val itemCategory : Categories? = item.categories?.get(0)
        holder.binding.apply {
            rowName.text = item.name
            rowDistance.text = item.distance.toString() + " m"

            itemCategory?.let { category ->
                rowCategory.text = category.shortName
                category.icon?.let {
                    (it.prefix != null && it.suffix != null).apply {
                        Glide.with(context)
                            .load(it.prefix + "64" + it.suffix)
                            .circleCrop()
                            .into(holder.binding.rowImage)
                    }
                }
            }

            rowItem.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra(Utils.INTENT.DETAIL, item)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}