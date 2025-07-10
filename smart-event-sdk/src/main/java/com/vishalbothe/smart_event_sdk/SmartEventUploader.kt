package com.vishalbothe.smart_event_sdk

import android.util.Log
import java.time.temporal.IsoFields

class SmartEventUploader {

    // THIS IS SIMULATED SERVER MEMORY

    private val uploadedEvents = mutableListOf<EventEntity>()

    fun getUploadedEvents(): List<EventEntity> = uploadedEvents.toList()

    fun upload(events: List<EventEntity>): Pair<List<String>, List<String>> {
        val success = mutableListOf<String>()
        val failed = mutableListOf<String>()

        for (event in events) {

            // CHECKING WITH THE RANDOM NUMBER SINCE THIS IS THE MOCK SERVER
            val isSuccessful = Math.random() > 0.2
            if (isSuccessful) {
                uploadedEvents.add(event)
                success.add(event.id)
                Log.d("Uploader", "Uploaded event: ${event.id}")
            } else {
                failed.add(event.id)
                Log.d("Uploader", "Failed to upload event: ${event.id}")
            }
        }
        return Pair(success, failed)
    }
}