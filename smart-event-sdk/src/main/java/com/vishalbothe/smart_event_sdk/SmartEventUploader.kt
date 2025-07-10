package com.vishalbothe.smart_event_sdk

class SmartEventUploader {

    private val api: SmartEventApi = MockSmartEventApi()

    fun upload(events: List<EventEntity>): Pair<List<String>, List<String>> {
        return try {
            val response = api.uploadEvents(events).execute()
            if (response.isSuccessful && response.body() != null) {
                val responseBody = response.body()!!
                Pair(responseBody.successIds, responseBody.failedIds)
            } else {
                Pair(emptyList(), emptyList())
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Pair(emptyList(), emptyList())
        }
    }
}