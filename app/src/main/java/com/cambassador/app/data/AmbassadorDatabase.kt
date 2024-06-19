package com.cambassador.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration2To3 : Migration(2,3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE event ADD COLUMN url TEXT DEFAULT ''")
    }
}

@Database(entities = [Event::class, Code::class], version = 3, exportSchema = false)
abstract class AmbassadorDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao
    abstract fun codeDao(): CodeDao

    companion object {
        @Volatile
        private var Instance: AmbassadorDatabase? = null

        fun getDatabase(context: Context): AmbassadorDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AmbassadorDatabase::class.java, "ambassador_database")
                    .addMigrations(Migration2To3())
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
