package com.cambassador.app.ui.menu

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cambassador.app.AmbassadorTopAppBar
import com.cambassador.app.R
import com.cambassador.app.data.exporter.CsvExporter
import com.cambassador.app.data.importer.CsvImporter
import com.cambassador.app.ui.navigation.NavigationDestination
import androidx.compose.ui.platform.LocalContext
import com.cambassador.app.data.AmbassadorDatabase
import kotlinx.coroutines.launch

object MenuScreenDestination : NavigationDestination {
    override val route = "menu_screen"
    override val titleRes = R.string.menu
}
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
    fun MenuScreen(
    navigateToUsers: () -> Unit,
    onNavigateUp: () -> Unit,
    navController: NavController
){
    Scaffold(
        topBar = {
            AmbassadorTopAppBar(
                title = stringResource(R.string.menu),
                canNavigateBack = true,
                navigateUp = onNavigateUp,
                menu = false.toString(),
                navController = navController
            )
        }
    ){
        MenuBody(navigateToUsers = navigateToUsers)
    }
}

@Composable
fun MenuBody(
    navigateToUsers: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope =  rememberCoroutineScope()
    val database = remember {
        AmbassadorDatabase.getDatabase(context.applicationContext)
    }

    // CsvExporterとCsvImporterのインスタンスもrememberで保持
    val csvExporter = remember { CsvExporter(database) }
    val csvImporter = remember { CsvImporter(database) }

    // 処理中の状態を管理 (エクスポート/インポート共通)
    var isProcessing by remember { mutableStateOf(false) }

    // エクスポート完了メッセージ用の状態
    var exportMessage by remember { mutableStateOf<String?>(null) }
    // インポート完了メッセージ用の状態
    var importMessage by remember { mutableStateOf<String?>(null) }
    // インポート確認アラート表示の状態
    var showImportConfirmationDialog by remember { mutableStateOf(false) }
    // インポートを待機しているURI
    var pendingImportUri by remember { mutableStateOf<Uri?>(null) }

    // エクスポート完了時のToast表示
    if (exportMessage != null) {
        LaunchedEffect(exportMessage!!) {
            Toast.makeText(context, exportMessage, Toast.LENGTH_LONG).show()
            exportMessage = null // メッセージ表示後にクリア
        }
    }

    // インポート完了時のToast表示
    if (importMessage != null) {
        LaunchedEffect(importMessage!!) {
            Toast.makeText(context, importMessage, Toast.LENGTH_LONG).show()
            importMessage = null // メッセージ表示後にクリア
        }
    }

    // CSVファイルを保存するためのランチャー
    val exportCsvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri: Uri? ->
        uri?.let {
            isProcessing = true // 処理開始
            coroutineScope.launch {
                try {
                    csvExporter.exportAllTablesToCsv(context, it)
                    exportMessage = "CSVエクスポートが完了しました"
                } catch (e: Exception) {
                    exportMessage = "CSVエクスポートに失敗しました: ${e.localizedMessage}"
                    e.printStackTrace()
                } finally {
                    isProcessing = false // 処理終了
                }
            }
        }
    }

    // CSVファイルをインポートするためのランチャー
    val importCsvLauncher =  rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            pendingImportUri = it // URIを一時保存
            showImportConfirmationDialog = true // アラートを表示
        }
    }

    // インポート確認アラート
    if (showImportConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showImportConfirmationDialog = false },
            title = { Text("データのインポート確認") },
            text = { Text("既存のすべてのデータは破棄されます。\nインポートを実行してもよろしいですか？") },
            confirmButton = {
                TextButton(onClick = {
                    showImportConfirmationDialog = false
                    pendingImportUri?.let { uri ->
                        isProcessing = true
                        coroutineScope.launch {
                            try {
                                csvImporter.importCsv(context, uri)
                                importMessage = "CSVインポートが完了しました"
                            } catch (e: Exception) {
                                importMessage = "CSVインポートに失敗しました: ${e.localizedMessage}"
                                e.printStackTrace()
                            } finally {
                                isProcessing = false
                                pendingImportUri = null
                            }
                        }
                    }
                }) {
                    Text("はい")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImportConfirmationDialog = false
                    pendingImportUri = null
                }) {
                    Text("キャンセル")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = navigateToUsers,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.user_name))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                exportCsvLauncher.launch("app_data_${System.currentTimeMillis()}.csv")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.export))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { importCsvLauncher.launch(arrayOf("text/csv")) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isProcessing && !showImportConfirmationDialog
        ) {
            Text(stringResource(R.string.import_csv))
        }
    }
    // 処理中のプログレス表示
    if (isProcessing) {
        Spacer(modifier = Modifier.height(16.dp))
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(8.dp))
        Text("Processing...")
    }
}
