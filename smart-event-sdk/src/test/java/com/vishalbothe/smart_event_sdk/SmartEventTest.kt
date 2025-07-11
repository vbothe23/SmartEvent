package com.vishalbothe.smart_event_sdk

import android.content.Context
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SmartEventUnitTest {
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private val mockStorage: SmartEventStorage = mockk(relaxed = true)
    private val mockUploader: SmartEventUploader = mockk(relaxed = true)
    private val mockContext: Context = mockk(relaxed = true)
    private val mockEventListener: SmartEventListener = mockk(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        SmartEvent.setDispatcherForTesting(testDispatcher)
        // Use reflection to set private fields
        SmartEvent.javaClass.getDeclaredField("isInitialized").apply { isAccessible = true }.set(SmartEvent, false)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `init initializes storage and uploader only once`() {
        SmartEvent.init(mockContext)
        val storage1 = SmartEvent.javaClass.getDeclaredField("storage").apply { isAccessible = true }.get(SmartEvent)
        val uploader1 = SmartEvent.javaClass.getDeclaredField("uploader").apply { isAccessible = true }.get(SmartEvent)
        // Call init again, should not re-initialize
        SmartEvent.init(mockContext)
        val storage2 = SmartEvent.javaClass.getDeclaredField("storage").apply { isAccessible = true }.get(SmartEvent)
        val uploader2 = SmartEvent.javaClass.getDeclaredField("uploader").apply { isAccessible = true }.get(SmartEvent)
        assertEquals(storage1, storage2)
        assertEquals(uploader1, uploader2)
    }

    @Test
    fun `setEventFilter and setEventListener set the correct callbacks`() {
        SmartEvent.init(mockContext)
        SmartEvent.setEventListener(mockEventListener)
        val testFilter: (String, Map<String, Any>?) -> Boolean = { _, _ -> false }
        SmartEvent.setEventFilter(testFilter)
        val filterField = SmartEvent.javaClass.getDeclaredField("eventFilter").apply { isAccessible = true }.get(SmartEvent)
        val listenerField = SmartEvent.javaClass.getDeclaredField("eventListener").apply { isAccessible = true }.get(SmartEvent)
        assertEquals(testFilter, filterField)
        assertEquals(mockEventListener, listenerField)
    }

    @Test
    fun `log throws if not initialized`() = testScope.runTest {
        SmartEvent.javaClass.getDeclaredField("isInitialized").apply { isAccessible = true }.set(SmartEvent, false)
        assertThrows(IllegalStateException::class.java) {
            SmartEvent.log("event", null)
        }
    }

    @Test
    fun `log calls storage and listener if filter passes`() = testScope.runTest {
        SmartEvent.init(mockContext)
        SmartEvent.setEventListener(mockEventListener)
        SmartEvent.javaClass.getDeclaredField("storage").apply { isAccessible = true }.set(SmartEvent, mockStorage)
        SmartEvent.javaClass.getDeclaredField("eventFilter").apply { isAccessible = true }.set(SmartEvent, null)
        every { mockStorage.insertEvent(any(), any()) } returns "event-id"
        coEvery { mockEventListener.onEventStored(any()) } just Runs
        SmartEvent.log("test", mapOf("a" to 1))
        testDispatcher.scheduler.advanceUntilIdle()
        verify { mockStorage.insertEvent("test", mapOf("a" to 1)) }
        verify { mockEventListener.onEventStored("event-id") }
    }

    @Test
    fun `log does not call storage if filter blocks`() = testScope.runTest {
        SmartEvent.init(mockContext)
        SmartEvent.setEventListener(mockEventListener)
        SmartEvent.javaClass.getDeclaredField("storage").apply { isAccessible = true }.set(SmartEvent, mockStorage)
        SmartEvent.setEventFilter { _, _ -> false }
        SmartEvent.log("blocked", null)
        testDispatcher.scheduler.advanceUntilIdle()
        verify(exactly = 0) { mockStorage.insertEvent(any(), any()) }
    }

    @Test
    fun `flush calls uploader and marks events as synced`() = testScope.runTest {
        SmartEvent.init(mockContext)
        SmartEvent.setEventListener(mockEventListener)
        SmartEvent.javaClass.getDeclaredField("storage").apply { isAccessible = true }.set(SmartEvent, mockStorage)
        SmartEvent.javaClass.getDeclaredField("uploader").apply { isAccessible = true }.set(SmartEvent, mockUploader)
        val events = listOf(EventEntity("id1", "e1", "{}", 0L, false))
        every { mockStorage.getUnSyncedEvents() } returns events
        every { mockUploader.upload(events) } returns Pair(listOf("id1"), emptyList())
        every { mockStorage.markEventAsSynced(listOf("id1")) } just Runs
        coEvery { mockEventListener.onFlushCompleted(any(), any()) } just Runs
        SmartEvent.flush()
        testDispatcher.scheduler.advanceUntilIdle()
        verify { mockStorage.getUnSyncedEvents() }
        verify { mockUploader.upload(events) }
        verify { mockStorage.markEventAsSynced(listOf("id1")) }
        verify { mockEventListener.onFlushCompleted(1, 0) }
    }

    @Test
    fun `flush calls onFlushCompleted with 0 if no unsynced events`() = testScope.runTest {
        SmartEvent.init(mockContext)
        SmartEvent.setEventListener(mockEventListener)
        SmartEvent.javaClass.getDeclaredField("storage").apply { isAccessible = true }.set(SmartEvent, mockStorage)
        SmartEvent.javaClass.getDeclaredField("uploader").apply { isAccessible = true }.set(SmartEvent, mockUploader)
        every { mockStorage.getUnSyncedEvents() } returns emptyList()
        coEvery { mockEventListener.onFlushCompleted(any(), any()) } just Runs
        SmartEvent.flush()
        testDispatcher.scheduler.advanceUntilIdle()
        verify { mockStorage.getUnSyncedEvents() }
        verify { mockEventListener.onFlushCompleted(0, 0) }
        verify(exactly = 0) { mockUploader.upload(any()) }
    }
}