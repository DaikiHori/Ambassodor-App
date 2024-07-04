package com.cambassador.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration3To4 : Migration(3,4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE users (" +
                " id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " name TEXT NOT NULL " +
                ");")
    }
}

@Database(entities = [Event::class, Code::class, User::class], version = 4, exportSchema = false)
abstract class AmbassadorDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao
    abstract fun codeDao(): CodeDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var Instance: AmbassadorDatabase? = null

        fun getDatabase(context: Context): AmbassadorDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AmbassadorDatabase::class.java, "ambassador_database")
                    .addMigrations(Migration3To4())
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
