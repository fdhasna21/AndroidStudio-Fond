package com.fdhasna21.fond.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.parcelize.Parcelize

/**
 * Created by Fernanda Hasna on 27/10/2024.
 */

@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
class ItemGallery (
    var prefix : String? = null,
    var suffix : String? = null,
    var width : Int? = null,
    var height : Int? = null
): Parcelable