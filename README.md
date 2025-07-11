# Smart Event SDK

---

## Overview

The **SmartEvent SDK** is a lightweight, reusable Android library designed to help applications log **custom user-defined events** with support for:
- **Offline logging**
- **Event persistence**
- **Batched uploads**

---

## SmartEvent SDK as a Reusable Module

- Created as an **Android library module**, separate from the app.
- Exposes a public API via the `SmartEvent` object.
- ### File Structure
    - EntityEvent.kt           - Data class representing an event
    - MockSmartEventApi.kt     - A mock implementation of the Server
    - SmartEvent.kt            - The SDK entrypoint
    - SmartEventApi.kt         - Retrofit interface defining the endpoint
    - SmartEventDbHelper.kt    - SQLiteOpenHelper class for creating and upgrading the local SQLite database
    - SmartEventListener.kt    - SDK interface for clients to receive callbacks when events are stored or flush completes
    - SmartEventStorage.kt     - Handles inserting, querying, and updating event records in the local SQLite database
    - SmartEventUploader.kt    - Uploads batched unsynced events to the Retrofit-based mock server and returns IDs of successfully uploaded events

### SmartEvent Demo App

- Implements event logging using the SDK.
- **User flow**:
  - User enters event name and properties.
  - On **Log Event** button click, events are stored locally in a database.
  - On **Flush** button click, events are uploaded to a server (mock server in the demo).
- **Custom event filtering** implemented:
  - Events named `"debug_event"` are filtered out.
- **EventListener interface** implemented:
  - Demo app observes and displays event status updates on-screen.

---

## Unit Testing

- Implemented unit tests for the key SDK functions:
  - `SmartEvent.log()`
  - `SmartEvent.flush()`

---

## Design Docs

### How is persistence handled?

- Events are persisted using a lightweight custom SQLite implementation (`SmartEventStorage`).
- A database file stores:
  - Event ID
  - Event name
  - Properties
  - Timestamp
  - Sync status
- Events are marked as "synced" after a successful flush.

### How is thread-safety ensured?

- All background work dispatched on `Dispatcher.IO` using a `CoroutineScope`.
- UI thread interactions are dispatched to `Dispatcher.Main`.

### What makes the SDK safe for repeated integration?

- Simple, lightweight Kotlin SDK with clear public APIs:
  - `SmartEvent.init()`
  - `SmartEvent.log()`
  - `SmartEvent.flush()`
- Consumers can customize SDK behavior via:
  - `SmartEvent.setEventFilter { ... }`
  - `SmartEvent.setEventListener(...)`
- **No external analytics or network dependencies required.**

---

## Public API

```kotlin
SmartEvent.init(context)
SmartEvent.log(eventName, properties)
SmartEvent.flush()
SmartEvent.setEventFilter { event -> /* filter logic */ }
SmartEvent.setEventListener(listener)
