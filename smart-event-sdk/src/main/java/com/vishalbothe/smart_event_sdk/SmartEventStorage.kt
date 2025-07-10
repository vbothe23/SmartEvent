package com.vishalbothe.smart_event_sdk

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.google.gson.Gson
import java.util.UUID

class SmartEventStorage(context: Context) {

    private val dbHelper = SmartEventDbHelper(context.applicationContext)
    private val gson = Gson()

    fun insertEvent(eventName: String, properties: Map<String, Any>?): String {
        val database = dbHelper.writableDatabase

        val eventId = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()

        val propertiesJson = gson.toJson(properties ?: emptyMap<String, Any>())

        val values = ContentValues().apply {
            put("id", eventId)
            put("name", eventName)
            put("property", propertiesJson)
            put("timestamp", timestamp)
            put("synced", 0)
        }

        database.insert(SmartEventDbHelper.TABLE_EVENTS, null, values)
        return eventId
    }

    fun getUnSyncedEvents(limit: Int = 50): List<EventEntity> {
        val database = dbHelper.readableDatabase
        val events = mutableListOf<EventEntity>()

        val cursor: Cursor = database.query(
            SmartEventDbHelper.TABLE_EVENTS,
            null,
            "synced = ?",
            arrayOf("0"),
            null,
            null,
            "timestamp ASC",
            limit.toString()
        )

        while (cursor.moveToNext()) {
            val event = EventEntity(
                id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
                eventName = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                propertiesJsonString = cursor.getString(cursor.getColumnIndexOrThrow("properties")),
                timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")),
                isSynced = cursor.getInt(cursor.getColumnIndexOrThrow("synced")) == 1
            )
            events.add(event)
        }
        cursor.close()
        return events
    }

    fun markEventAsSynced(eventsIds: List<String>) {
        val database = dbHelper.writableDatabase
        database.beginTransaction()

        try {
            for (id in eventsIds) {
                val values = ContentValues().apply {
                    put("synced", 1)
                }
                database.update(
                    SmartEventDbHelper.TABLE_EVENTS,
                    values,
                    "id = ?",
                    arrayOf(id)
                )
            }
            database.setTransactionSuccessful()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            database.endTransaction()
        }

    }
}