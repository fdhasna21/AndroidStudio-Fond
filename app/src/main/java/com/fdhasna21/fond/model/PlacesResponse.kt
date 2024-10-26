package com.fdhasna21.fond.model.response

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
class PlacesResponse(
    var results : ArrayList<ItemResto>? = arrayListOf()
) : Parcelable

@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
class ItemResto(
    @SerializedName("fsq_id")
    var fsqId : String? = "-",
    var categories : ArrayList<Categories>? = arrayListOf(),
    var distance : Int? = 0,
    var geocodes : Geocodes? = null,
    var location : Location? = null,
    var name : String? = "-",
    var timezone : String? = "-"
) : Parcelable

@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
class Geocodes(
    var main : Main? = null
): Parcelable

@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
class Main(
    var longitude : Double? = 0.0,
    var latitude : Double? = 0.0
) : Parcelable

@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
class Location(
    var address : String? = "-",
    var country : String? = "-",
    @SerializedName("cross_street")
    var crossStreet : String? = null,
    var postcode : String? = null,
    var region : String? = "-"
) : Parcelable

@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
class Categories(
    @SerializedName("short_name")
    var shortName: String? = "-",
    var icon: Icon? = null
) : Parcelable

@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
class Icon(
    var prefix: String? = null,
    var suffix: String? = null
) : Parcelable