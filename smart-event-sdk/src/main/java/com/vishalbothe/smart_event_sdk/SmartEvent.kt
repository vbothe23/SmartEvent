package com.vishalbothe.smart_event_sdk

import android.content.Context
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

object SmartEvent {

    private lateinit var storage: SmartEventStorage
    private lateinit var uploader: SmartEventUploader

    private var eventFilter: ((String, Map<String, Any>?) -> Boolean)? = null
    private var eventListener: SmartEventListener? = null

    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return
        storage = SmartEventStorage(context)

        uploader = SmartEventUploader()
        isInitialized = true
    }

    fun setEventFilter(filter: (String, Map<String, Any>?) -> Boolean) {
        this.eventFilter = filter
    }

    fun setEventListener(listener: SmartEventListener) {
        this.eventListener = listener
    }

    fun log(eventName: String, properties: Map<String, Any>?) {

        if (!isInitialized) throw IllegalStateException("SmartEvent is not initialized!!")

        val shouldLogEvent = eventFilter?.invoke(eventName, properties) ?: true
        if (!shouldLogEvent) return

        CoroutineScope(Dispatchers.IO).launch {
            val eventId = storage.insertEvent(eventName, properties)
            withContext(Dispatchers.Main) {
                eventListener?.onEventStored(eventId)
            }
        }
    }

    fun flush() {
        if (!isInitialized) throw IllegalStateException("smartEvent not initialized!!")

        CoroutineScope(Dispatchers.IO).launch {
            val unSyncedEvents = storage.getUnSyncedEvents()
            if (unSyncedEvents.isEmpty()) {
                withContext(Dispatchers.Main) {
                    eventListener?.onFlushCompleted(0, 0)
                }
                return@launch
            }

            val (successIds, failedIds) = uploader.upload(unSyncedEvents)
            storage.markEventAsSynced(successIds)

            withContext(Dispatchers.Main) {
                eventListener?.onFlushCompleted(successIds.size, failedIds.size)
            }
        }
    }
}