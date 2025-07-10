package com.vishalbothe.smart_event_sdk

import retrofit2.Call
import retrofit2.mock.Calls

class MockSmartEventApi: SmartEventApi  {

    private val uploadedEvents = mutableListOf<EventEntity>()

     override fun uploadEvents(events: List<EventEntity>): Call<UploadResponse> {

         val successIds = mutableListOf<String>()
         val failedIds = mutableListOf<String>()

         events.forEach {

             val isSuccess = Math.random() > 0.2

             if (isSuccess) {
                 uploadedEvents.add(it)
                 successIds.add(it.id)
             } else {
                 failedIds.add(it.id)
             }
         }

         val response = UploadResponse(successIds, failedIds)
         return Calls.response(response)
    }

}