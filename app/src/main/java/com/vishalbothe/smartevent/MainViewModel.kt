package com.vishalbothe.smartevent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun logEvent(eventName: String, props: Map<String, Any>) {
        viewModelScope.launch {
            if (eventName.isBlank()) {
                _status.value = "Please enter event name"
                return@launch
            }

            SmartEvent.log(eventName, props)
        }
    }

    fun flushEvents() {
        SmartEvent.flush()
    }
}