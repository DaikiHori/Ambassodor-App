package com.cambassador.app.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration5To6 : Migration(5,6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE users2 (" +
                " id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                " name TEXT NOT NULL UNIQUE " +
                ");")
        database.execSQL("DROP TABLE users")
        database.execSQL("ALTER TABLE users2 RENAME TO users")
    }
}

@Database(
    entities = [Event::class, Code::class, User::class],
    version = 6,
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
                    .addMigrations(Migration5To6())
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
