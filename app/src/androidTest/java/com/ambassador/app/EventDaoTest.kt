package com.ambassador.app

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ambassador.app.data.AmbassadorDatabase
import com.ambassador.app.data.Event
import com.ambassador.app.data.EventDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.Date

@RunWith(AndroidJUnit4::class)
class EventDaoTest {

    private lateinit var eventDao: EventDao
    private lateinit var ambassadorDatabase: AmbassadorDatabase
    private val event1 = Event(1, "Apples", Date())
    private val event2 = Event(2, "Bananas", Date())

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        ambassadorDatabase = Room.inMemoryDatabaseBuilder(context, AmbassadorDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        eventDao = ambassadorDatabase.eventDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        ambassadorDatabase.close()
    }

    @Test
    @Throws(Exception::class)
    fun daoInsert_insertsEventIntoDB() = runBlocking {
        addOneEventToDb()
        val allEvents = eventDao.getAllEvents().first()
        assertEquals(allEvents[0], event1)
    }

    @Test
    @Throws(Exception::class)
    fun daoGetAllEvents_returnsAllEventsFromDB() = runBlocking {
        addTwoEventsToDb()
        val allEvents = eventDao.getAllEvents().first()
        assertEquals(allEvents[0], event1)
        assertEquals(allEvents[1], event2)
    }


    @Test
    @Throws(Exception::class)
    fun daoGetEvent_returnsEventFromDB() = runBlocking {
        addOneEventToDb()
        val event = eventDao.getEvent(1)
        assertEquals(event.first(), event1)
    }

    @Test
    @Throws(Exception::class)
    fun daoDeleteEvents_deletesAllEventsFromDB() = runBlocking {
        addTwoEventsToDb()
        eventDao.delete(event1)
        eventDao.delete(event2)
        val allEvents = eventDao.getAllEvents().first()
        assertTrue(allEvents.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun daoUpdateEvents_updatesEventsInDB() = runBlocking {
        addTwoEventsToDb()
        eventDao.update(Event(1, "Apples", Date()))
        eventDao.update(Event(2, "Bananas", Date()))

        val allEvents = eventDao.getAllEvents().first()
        assertEquals(allEvents[0], Event(1, "Apples", Date()))
        assertEquals(allEvents[1], Event(2, "Bananas", Date()))
    }

    private suspend fun addOneEventToDb() {
        eventDao.insert(event1)
    }

    private suspend fun addTwoEventsToDb() {
        eventDao.insert(event1)
        eventDao.insert(event2)
    }
}
