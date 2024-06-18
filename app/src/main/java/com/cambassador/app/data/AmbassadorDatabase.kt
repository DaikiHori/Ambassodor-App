/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
            // if the Instance is not null, return it, otherwise create a new database instance.
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
