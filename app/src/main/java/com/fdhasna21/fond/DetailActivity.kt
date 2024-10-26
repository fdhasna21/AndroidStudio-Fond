package com.fdhasna21.fond

import android.view.View
import android.view.View.OnClickListener
import com.bumptech.glide.Glide
import com.fdhasna21.fond.base.BaseActivity
import com.fdhasna21.fond.databinding.ActivityDetailBinding
import com.fdhasna21.fond.model.response.ItemResto
import com.fdhasna21.fond.utility.Utils
import com.google.android.material.bottomsheet.BottomSheetBehavior

/**
 * Created by Fernanda Hasna on 26/10/2024.
 */

class DetailActivity : BaseActivity<ActivityDetailBinding>(ActivityDetailBinding::inflate), OnClickListener {

    private lateinit var resto : ItemResto

    override fun setupData() {
        resto = intent.getParcelableExtra<ItemResto>(Utils.INTENT.DETAIL) ?: ItemResto()
    }

    override fun setupUI() {
        binding.apply {
            setSupportActionBar(topAppBar)
            supportActionBar?.apply {
                setHomeAsUpIndicator(R.drawable.ic_left);
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(false)
            }

            resto.apply {
                addressMore.setOnClickListener(this@DetailActivity)
                galleryMore.setOnClickListener(this@DetailActivity)

                detailName.text = name
                detailDistance.text = distance.toString() + " m"

                location?.let{ loc ->
                    val fullAddress = loc.address + if(loc.crossStreet != null && loc.crossStreet != "" && loc.crossStreet != "null"){
                        "(" + loc.crossStreet + ")"
                    } else {
                        ""
                    }
                    address.text = fullAddress
                    region.text = loc.region
                    val fullCountry  = if(loc.postcode != null && loc.postcode != "" && loc.postcode != "null"){
                        loc.postcode + ", " + loc.country
                    } else {
                        loc.country
                    }
                    country.text = fullCountry
                }

                categories?.get(0)?.let { category ->
                    detailCategory.text = category.shortName
                    category.icon?.let {
                        (it.prefix != null && it.suffix != null).apply {
                            Glide.with(this@DetailActivity)
                                .load(it.prefix + "64" + it.suffix)
                                .circleCrop()
                                .into(rowImage)
                        }
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        binding.apply {
            when (v?.id) {
                addressMore.id -> {
                    if(layoutAddress.visibility == View.VISIBLE){
                        layoutAddress.visibility = View.GONE
                        addressMore.setImageResource(R.drawable.ic_expand)
                    } else {
                        layoutAddress.visibility = View.VISIBLE
                        addressMore.setImageResource(R.drawable.ic_collapse)
                    }
                }

                galleryMore.id -> {
                    if(recyclerView.visibility == View.VISIBLE){
                        recyclerView.visibility = View.GONE
                        galleryMore.setImageResource(R.drawable.ic_expand)
                    } else {
                        recyclerView.visibility = View.VISIBLE
                        galleryMore.setImageResource(R.drawable.ic_collapse)
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}