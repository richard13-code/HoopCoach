package com.example.hoopcoach.core.network

import com.example.hoopcoach.core.model.DrillResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response

interface DrillAPI {
    @GET("/")
    suspend fun getDrills(
        @Query("format") format: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Response<DrillResponse>

}