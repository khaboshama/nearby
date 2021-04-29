package com.khaled.nearbyapp.model.network.endPoints

import com.khaled.nearbyapp.BuildConfig
import com.khaled.nearbyapp.constant.Constants
import com.khaled.nearbyapp.model.network.response.VenueListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface VenuesApi {

    @GET("venues/explore")
    suspend fun getVenueList(
        @Query("client_id") clientId: String = BuildConfig.CLIENT_ID,
        @Query("client_secret") clientSecret: String = BuildConfig.CLIENT_SECRET,
        @Query("v") version: Long = Constants.API_VERSION,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int = 0,
        @Query("ll") latLong: String,
        @Query("sortByDistance") sortByDistance: Int = 1,
    ): Response<VenueListResponse>

}