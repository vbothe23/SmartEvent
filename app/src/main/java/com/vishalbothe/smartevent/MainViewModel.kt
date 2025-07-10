package com.vishalbothe.smartevent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vishalbothe.smart_event_sdk.SmartEvent
import com.vishalbothe.smart_event_sdk.SmartEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    private val _status = MutableStateFlow("IDLE")
    val status: StateFlow<String> = _status

    init {
        SmartEvent.setEventListener(
            object : SmartEventListener {
                override fun onEventStored(eventId: String) {
                    _status.value = "Event Stored $eventId"
                }

                override fun onFlushCompleted(successCount: Int, failedCount: Int) {
                    _status.value = "Flush: $successCount success, $failedCount failed"
                }
            }
        )
    }

    fun logEvent(eventName: String, propsJson: String) {
        viewModelScope.launch {
            if (eventName.isBlank()) {
                _status.value = "Please enter event name"
                return@launch
            }

            val props: Map<String, Any>? = if (propsJson.isNotBlank()) {
                try {
                    val type = object : TypeToken<Map<String, Any>>() {}.type
                    Gson().fromJson<Map<String, Any>>(propsJson, type)
                } catch (e: Exception) {
                    _status.value = "Invalid JSON!"
                    return@launch
                }
            } else null

            SmartEvent.log(eventName, props)
        }
    }

    fun flushEvents() {
        SmartEvent.flush()
    }
}