package com.vishalbothe.smart_event_sdk

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class SmartEventDbHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_EVENTS (
                id TEXT PRIMARY KEY,
                name TEXT,
                properties TEXT,
                timestamp INTEGER,
                synced INTEGER
            );
        """.trimIndent()
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_EVENTS")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "smart_event.db"
        const val DATABASE_VERSION = 1
        const val TABLE_EVENTS = "events"

    }
}