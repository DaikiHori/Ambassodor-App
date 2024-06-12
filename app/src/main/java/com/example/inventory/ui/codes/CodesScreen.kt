package com.example.inventory.ui.codes

import android.graphics.Bitmap
import android.util.Log
import android.widget.ToggleButton
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.job
import kotlinx.coroutines.launch

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
    onSaveEnd: (Int) ->Unit,
    modifier: Modifier = Modifier,
    viewModel: CodesViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        topBar = {
            InventoryTopAppBar(
                title = stringResource(CodesDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        }
    ) { innerPadding ->
            CodesBody(
                codesUiState = viewModel.codesUiState,
                onValueChange = viewModel::updateUiState,
                navigateBack = navigateBack,
                onSaveClick = {
                    coroutineScope.launch {
                        viewModel.updateCode()
                        if(viewModel.codesUiState.codesDetails.code.isNotEmpty()) {
                            onSaveEnd(viewModel.codesUiState.codesDetails.eventId)
                        } else {
                            navigateBack()
                        }
                    }
                },
                viewModel = viewModel,
                modifier = Modifier
                    .padding(
                        start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                        top = innerPadding.calculateTopPadding(),
                        end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    ),
                contentPadding = innerPadding,
            )

    }
}
@Composable
private fun CodesBody(
    codesUiState: CodesUiState,
    onValueChange: (CodesDetails) -> Unit,
    navigateBack: () -> Unit,
    onSaveClick: () -> Unit,
    viewModel: CodesViewModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
    ) {
        CodeView(
            codesUiState = codesUiState,
            onValueChange = viewModel::updateUiState,
            onSaveClick = onSaveClick,
            navigateBack = navigateBack,
            modifier = Modifier
                .padding(horizontal = dimensionResource(id = R.dimen.padding_small))
        )
    }
}

@Composable
private fun CodeView(
    codesUiState: CodesUiState,
    onValueChange: (CodesDetails) -> Unit,
    onSaveClick:() -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier
){

    Column(modifier = Modifier.fillMaxHeight()) {
        if(codesUiState.codesDetails.code.isNotEmpty()) {
            AsyncImage(
                model = qrCode(stringResource(R.string.url) + (codesUiState.codesDetails.code)),
                contentDescription = null,
            )
        }else{
            //navigateBack()
        }
        Text(codesUiState.codesDetails.code)
        CodeEditForm(
            codesDetails = codesUiState.codesDetails,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save_action))
        }
    }
}

@Composable
fun CodeEditForm(
    codesDetails: CodesDetails,
    onValueChange: (CodesDetails) -> Unit,
    modifier: Modifier
){
    val code = codesDetails
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        Text(stringResource(R.string.usable))
        Switch(checked = code.usable,
            onCheckedChange = {
                onValueChange(code.copy(usable = it))
            })
        Text(stringResource(R.string.used))
        Switch(checked = code.used,
            onCheckedChange = {
                onValueChange(code.copy(used = it))
            })
        OutlinedTextField(
            value = code.userName,
            onValueChange = { onValueChange(code.copy(userName = it)) },
            label = { Text(stringResource(R.string.user_name)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
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
        700,
        700,
        hints
    )
}
fun createBitmap(bitMatrix: BitMatrix): Bitmap {
    val barcodeEncoder = BarcodeEncoder()
    return barcodeEncoder.createBitmap(bitMatrix)
}
