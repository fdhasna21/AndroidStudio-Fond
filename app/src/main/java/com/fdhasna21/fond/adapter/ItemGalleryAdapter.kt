package com.fdhasna21.fond.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fdhasna21.fond.R
import com.fdhasna21.fond.databinding.ItemGalleryBinding
import com.fdhasna21.fond.model.ItemGallery

/**
 * Created by Fernanda Hasna on 27/10/2024.
 */

class ItemGalleryAdapter(
    val context: Context,
    var data: ArrayList<ItemGallery>
) : RecyclerView.Adapter<ItemGalleryAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemGalleryBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.binding.apply {
            (item.prefix != null && item.suffix != null && item.width != null && item.height != null).let{
                (item.prefix != null && item.suffix != null).apply {
                    Glide.with(context)
                        .load(item.prefix + "120" + item.suffix)
                        .centerCrop()
                        .error(R.mipmap.ic_launcher)
                        .into(holder.binding.rowImage)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}