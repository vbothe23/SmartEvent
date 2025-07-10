package com.vishalbothe.smartevent

import android.app.Application
import com.vishalbothe.smart_event_sdk.SmartEvent

class DemoApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        SmartEvent.init(this)

        SmartEvent.setEventFilter { name, _ ->
            name != "debug_event" // To filter out the debug events
        }
    }
}