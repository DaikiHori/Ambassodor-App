package com.cambassador.app.data.importer

import android.content.Context
import android.net.Uri
import androidx.room.withTransaction
import com.cambassador.app.data.AmbassadorDatabase
import com.cambassador.app.data.Code
import com.cambassador.app.data.Event
import com.cambassador.app.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CsvImporter(private val database: AmbassadorDatabase) {

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    suspend fun importCsv(context: Context, uri: Uri) {
        withContext(Dispatchers.IO) {
            clearAllTables()
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    var line: String?
                    var currentTableName: String? = null
                    var header: List<String>? = null

                    while (reader.readLine().also { line = it } != null) {
                        val trimmedLine = line?.trim()
                        if (trimmedLine.isNullOrEmpty()) {
                            currentTableName = null
                            header = null
                            continue
                        }

                        val parts = parseCsvLine(trimmedLine)

                        if (parts.size >= 2 && parts[0].equals("table", ignoreCase = true)) {
                            currentTableName = parts[1].lowercase(Locale.ROOT)
                            header = null
                        } else if (currentTableName != null && header == null) {
                            header = parts.map { it.trim() }
                        } else if (currentTableName != null && header != null) {
                            insertDataIntoTable(currentTableName, header, parts)
                        }
                    }
                }
            }
        }
    }

    private suspend fun insertDataIntoTable(tableName: String, header: List<String>, data: List<String>) {
        when (tableName) {
            "codes" -> insertCode(header, data)
            "events" -> insertEvent(header, data)
            "users" -> insertUser(header, data)
            else -> {
                println("Unknown table name for import: $tableName")
            }
        }
    }

    private suspend fun insertCode(header: List<String>, data: List<String>) {
        val map = header.zip(data).toMap()
        val code = Code(
            number = map["number"]?.toIntOrNull() ?: 0,
            eventId = map["eventId"]?.toIntOrNull() ?: 0,
            code = map["code"] ?: "",
            usable = map["usable"]?.toBooleanStrictOrNull() ?: true,
            used = map["used"]?.toBooleanStrictOrNull() ?: false,
            userName = map["userName"] ?: ""
        )
        database.codeDao().insert(code)
    }

    private suspend fun insertEvent(header: List<String>, data: List<String>) {
        val map = header.zip(data).toMap()
        val dateString = map["date"]
        val parsedDate = try {
            dateString?.let { dateFormatter.parse(it) }
        } catch (e: Exception) {
            null
        } ?: Date(0)

        val event = Event(
            name = map["name"] ?: "",
            date = parsedDate,
            url = map["url"] ?: "",
            count = map["count"]?.toIntOrNull() ?: 0,
            usable_count = map["usable_count"]?.toIntOrNull() ?: 0
        )
        database.eventDao().insert(event)
    }

    private suspend fun insertUser(header: List<String>, data: List<String>) {
        val map = header.zip(data).toMap()
        val user = User(
            name = map["name"] ?: ""
        )
        database.userDao().insert(user)
    }

    private fun parseCsvLine(line: String): List<String> {
        return line.split(',').map { it.trim().removeSurrounding("\"").replace("\"\"", "\"") }
    }

    private suspend fun clearAllTables() {
        database.withTransaction { // トランザクション内でまとめて削除
            database.codeDao().deleteAll()
            database.eventDao().deleteAll()
            database.userDao().deleteAll()
            // 他のテーブルがあればここに追加
        }
    }
}