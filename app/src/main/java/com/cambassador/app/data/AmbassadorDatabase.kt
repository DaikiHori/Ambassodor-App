package com.cambassador.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration5To6 : Migration(5,6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE users2 (" +
                " id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                " name TEXT NOT NULL UNIQUE " +
                ");")
        db.execSQL("DROP TABLE users")
        db.execSQL("ALTER TABLE users2 RENAME TO users")
    }
}

class Migration6To7 : Migration(5,7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE new_code (" +
                " id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                " number INTEGER, eventId INTEGER, usable BOOLEAN, used BOOLEAN," +
                " userName String);")
        db.execSQL("INSERT INTO new_code (id, eventId, usable, used, username ) " +
                " SELECT id, eventId, usable, used, userName FROM codes")
        db.execSQL("DROP TABLE codes")
        db.execSQL("ALTER TABLE new_code RENAME TO codes")
    }
}

@Database(
    entities = [Event::class, Code::class, User::class],
    version = 7,
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
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
