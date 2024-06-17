package com.ambassador.app.ui.codes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.ambassador.app.AmbassadorTopAppBar
import com.ambassador.app.R
import com.ambassador.app.ui.AppViewModelProvider
import com.ambassador.app.ui.Utility
import com.ambassador.app.ui.navigation.NavigationDestination
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
            AmbassadorTopAppBar(
                title = stringResource(CodesDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack,
                navigateBack = navigateBack
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
        modifier = modifier
            .padding(dimensionResource(id = R.dimen.padding_medium))
            .padding(10.dp),
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
        Box (
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.CenterHorizontally)
        ){
            if (codesUiState.codesDetails.code.isNotEmpty()) {
                AsyncImage(
                    model = Utility.qrCode(stringResource(R.string.url) + (codesUiState.codesDetails.code)),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            } else {
                //navigateBack()
            }
        }
        Spacer(modifier = Modifier.height(1.dp))
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
        Row {
            Text(stringResource(R.string.usable),modifier = Modifier.padding(10.dp))
            Switch(checked = code.usable,
                onCheckedChange = {
                    onValueChange(code.copy(usable = it))
                })
        }
        Row {
            Text(stringResource(R.string.used),modifier = Modifier.padding(10.dp))
            Switch(checked = code.used,
                onCheckedChange = {
                    onValueChange(code.copy(used = it))
                })
        }
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