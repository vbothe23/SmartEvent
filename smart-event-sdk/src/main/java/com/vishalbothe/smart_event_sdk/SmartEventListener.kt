package com.vishalbothe.smart_event_sdk

interface SmartEventListener {
    fun onEventStored(eventId: String)
    fun onFlushCompleted(successCount: Int, failedCount: Int)
}