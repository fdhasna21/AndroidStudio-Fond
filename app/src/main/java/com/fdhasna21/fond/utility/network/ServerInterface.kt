package com.fdhasna21.fond.utility.network

import com.fdhasna21.fond.model.ItemGallery
import com.fdhasna21.fond.model.response.PlacesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by Fernanda Hasna on 26/10/2024.
 * Updated by Fernanda Hasna on 27/10/2024.
 */

interface ServerInterface {
    @GET("places/search")
    fun search(@Query("ll") location: String,
               @Query("radius") radius : String,
               @Query("categories") categories:String
    ) : Call<PlacesResponse>?

    @GET("places/search")
    fun searchByKeyword(@Query("query") keyword: String,
                        @Query("ll") location: String,
                        @Query("radius") radius : String,
                        @Query("categories") categories:String
    ) : Call<PlacesResponse>?

    @GET("places/{fsq_id}/photos")
    fun getGallery(@Path("fsq_id") fsqId: String) : Call<ArrayList<ItemGallery>>
}