package com.vishalbothe.smart_event_sdk

import java.sql.Timestamp

data class EventEntity (
    val id: String,
    val eventName: String,
    val propertiesJsonString: String,
    val timestamp: Long,
    val isSynced: Boolean = false
)