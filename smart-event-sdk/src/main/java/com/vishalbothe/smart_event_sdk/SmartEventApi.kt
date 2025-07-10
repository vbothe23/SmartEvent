package com.vishalbothe.smart_event_sdk

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface SmartEventApi {
    @POST("/events")
    fun uploadEvents(@Body events: List<EventEntity>): Call<UploadResponse>
}

data class UploadResponse(
    val successIds: List<String>,
    val failedIds: List<String>
)