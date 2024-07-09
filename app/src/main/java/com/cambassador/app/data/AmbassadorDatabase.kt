package com.cambassador.app.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration3To5 : Migration(3,5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE users (" +
                " id INT PRIMARY KEY AUTOINCREMENT, " +
                " name TEXT NOT NULL " +
                ");")
    }
}

@Database(
    entities = [Event::class, Code::class, User::class],
    version = 5,
    exportSchema = false
)
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
                    .fallbackToDestructiveMigration()
                    .addMigrations(Migration3To5())
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
