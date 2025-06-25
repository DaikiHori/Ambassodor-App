package com.cambassador.app.data.exporter

import android.content.Context
import android.net.Uri
import com.cambassador.app.data.AmbassadorDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Locale

class CsvExporter(private val database: AmbassadorDatabase) {

    // 日付フォーマットの定義
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    suspend fun exportAllTablesToCsv(context: Context, uri: Uri) {
        withContext(Dispatchers.IO) {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    // Codesテーブルのエクスポート
                    val codes = database.codeDao().getAllCodes().first()
                    exportTableToCsv(writer, "codes", codes) { code ->
                        // idを含む全てのフィールドをCSV行に生成
                        "${code.id},${code.number},${code.eventId},${escapeCsv(code.code)},${code.usable},${code.used},${escapeCsv(code.userName)}"
                    }
                    writer.append("\n\n") // 各テーブル間に空行を追加して区切りを明確に

                    // Eventsテーブルのエクスポート
                    val events = database.eventDao().getAllEvents().first()
                    exportTableToCsv(writer, "events", events) { event ->
                        // idを含む全てのフィールドをCSV行に生成。Date型はフォーマット
                        "${event.event.id},${escapeCsv(event.event.name)},${dateFormatter.format(event.event.date)},${escapeCsv(event.event.url)},${event.event.count},${event.event.usable_count}"
                    }
                    writer.append("\n\n")

                    // Usersテーブルのエクスポート
                    val users = database.userDao().getAllUsers().first()
                    exportTableToCsv(writer, "users", users) { user ->
                        // idを含む全てのフィールドをCSV行に生成
                        "${user.id},${escapeCsv(user.name)}"
                    }
                }
            }
        }
    }

    // 個別のテーブルをCSVとして書き込む汎用関数
    private fun <T> exportTableToCsv(
        writer: OutputStreamWriter,
        tableName: String,
        data: List<T>,
        getRowString: (T) -> String // 各オブジェクトからCSV行を生成するラムダ
    ) {
        // 1行目に table,tablename を追加
        writer.append("table,")
        writer.append(escapeCsv(tableName))
        writer.append("\n")

        // 2行目にヘッダーを追加 (全ての列を含む)
        when (tableName) {
            "codes" -> writer.append("id,number,eventId,code,usable,used,userName\n")
            "events" -> writer.append("id,name,date,url,count,usable_count\n")
            "users" -> writer.append("id,name\n")
            // 他のテーブルがある場合はここに追加
        }

        // データ行を書き込み
        data.forEach { item ->
            writer.append(getRowString(item))
            writer.append("\n")
        }
    }

    // CSVエスケープ処理
    private fun escapeCsv(field: String): String {
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"${field.replace("\"", "\"\"")}\""
        }
        return field
    }
}