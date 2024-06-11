package com.example.inventory.ui.codes

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.inventory.InventoryTopAppBar
import com.example.inventory.R
import com.example.inventory.data.Code
import com.example.inventory.data.EventAndCodes
import com.example.inventory.ui.AppViewModelProvider
import com.example.inventory.ui.home.HomeViewModel
import com.example.inventory.ui.navigation.NavigationDestination
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.journeyapps.barcodescanner.BarcodeEncoder

object CodesDestination : NavigationDestination {
    override val route = "codes"
    override val titleRes = R.string.codes
    const val eventIdArg = "eventId"
    val routeWithArgs = "$route/{$eventIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodesScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val coroutineScope = rememberCoroutineScope()
    val homeUiState by viewModel.homeUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            InventoryTopAppBar(
                title = stringResource(CodesDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        CodesBody(
            eventList = homeUiState.eventList,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding,
        )
    }
}
@Composable
private fun CodesBody(
    eventList: List<EventAndCodes>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        CodesList(
            eventList = eventList,
            contentPadding = contentPadding,
            modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
        )
    }
}
@Composable
private fun CodesList(
    eventList: List<EventAndCodes>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(items = eventList, key = { it.event.id }) { event ->
            for(e in event.codes) {
                CodesCode(
                    code = e,
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.padding_small))
                )
            }
        }
    }
}

@Composable
private fun CodesCode(
    code: Code,
    modifier: Modifier
){
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ){
        Column {
            Text(code.code)
            Spacer(Modifier.weight(1f))
            AsyncImage(
                model = qrCode(stringResource(R.string.url) + code.code),
                contentDescription = null,
            )
            Spacer(Modifier.weight(1f))
            Text(code.usable.toString())
            Spacer(Modifier.weight(1f))
            Text(code.used.toString())
            Spacer(Modifier.weight(1f))
            Text(code.userName)
        }
    }
}

fun qrCode(data: String): Bitmap? {
    val bitMatrix = createBitMatrix(data)
    return try {
        bitMatrix?.let { createBitmap(it) }
    }catch (e: Exception){
        e.message?.let { Log.d("bitmap_error", data) }
        null
    }
}

fun createBitMatrix(data: String): BitMatrix? {
    val multiFormatWriter = MultiFormatWriter()
    val hints = mapOf(
        EncodeHintType.MARGIN to 0,
        EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.L
    )
    return multiFormatWriter.encode(
        data,
        BarcodeFormat.QR_CODE,
        500,
        500,
        hints
    )
}
fun createBitmap(bitMatrix: BitMatrix): Bitmap {
    val barcodeEncoder = BarcodeEncoder()
    return barcodeEncoder.createBitmap(bitMatrix)
}
