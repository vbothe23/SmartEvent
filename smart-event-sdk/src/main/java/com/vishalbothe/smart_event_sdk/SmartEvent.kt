package com.vishalbothe.smart_event_sdk

import android.content.Context
import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors

object SmartEvent {

    private lateinit var storage: SmartEventStorage
    private lateinit var uploader: SmartEventUploader

    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

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

        executor.execute {
            val eventId = storage.insertEvent(eventName, properties)
            mainHandler.post {
                eventListener?.onEventStored(eventId)
            }
        }
    }

    fun flush() {
        if (!isInitialized) throw IllegalStateException("smartEvent not initialized!!")

        executor.execute {
            val unSyncedEvents = storage.getUnSyncedEvents()
            if (unSyncedEvents.isEmpty()) {
                mainHandler.post {
                    eventListener?.onFlushCompleted(0, 0)
                }
                return@execute
            }
            val (successIds, failedIds) = uploader.upload(unSyncedEvents)
            storage.markEventAsSynced(successIds)

            mainHandler.post {
                eventListener?.onFlushCompleted(successIds.size, failedIds.size)
            }
        }
    }
}