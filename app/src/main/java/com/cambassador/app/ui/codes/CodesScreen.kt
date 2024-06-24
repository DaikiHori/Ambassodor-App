package com.cambassador.app.ui.codes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.cambassador.app.AmbassadorTopAppBar
import com.cambassador.app.R
import com.cambassador.app.ui.AppViewModelProvider
import com.cambassador.app.ui.Utility
import com.cambassador.app.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

object CodesDestination : NavigationDestination {
    override val route = "codes"
    override val titleRes = R.string.code_long
    const val eventIdArg = "eventId"
    val routeWithArgs = "$route/{$eventIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodesScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    onSaveEnd: (Int) ->Unit,
    viewModel: CodesViewModel = viewModel(factory = AppViewModelProvider.Factory),
    menu: Boolean = true
){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        topBar = {
            AmbassadorTopAppBar(
                title = stringResource(CodesDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp,
                menu = menu,
                url = viewModel.eventUiState.eventDetails.url
           )
        }
    ) { innerPadding ->
            CodesBody(
                codesUiState = viewModel.codesUiState,
                onValueChange = viewModel::updateUiState,
                navigateBack = navigateBack,
                viewModel = viewModel,
                modifier = Modifier
                    .padding(
                        start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                        top = innerPadding.calculateTopPadding(),
                        end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    ),
                onSaveEnd= onSaveEnd
            )
    }
}

@Composable
private fun CodesBody(
    codesUiState: CodesUiState,
    onValueChange: (CodesDetails) -> Unit,
    navigateBack: () -> Unit,
    viewModel: CodesViewModel,
    modifier: Modifier = Modifier,
    onSaveEnd: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(dimensionResource(id = R.dimen.padding_medium))
            .padding(10.dp),
    ) {
        val coroutineScope = rememberCoroutineScope()
        Column(modifier = Modifier.fillMaxHeight()) {
            if(codesUiState.codesDetails.code.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    if (codesUiState.codesDetails.code.isNotEmpty() && !codesUiState.codesDetails.used && codesUiState.codesDetails.usable) {
                        AsyncImage(
                            model = Utility.qrCode(stringResource(R.string.url) + (codesUiState.codesDetails.code)),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(1.dp))
                Text(codesUiState.codesDetails.code, modifier = Modifier.padding(10.dp))
                val code = codesUiState.codesDetails
                Column(
                    modifier = modifier,
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
                ) {
                    Row {
                        Text(stringResource(R.string.usable), modifier = Modifier.padding(10.dp))
                        Switch(checked = code.usable,
                            onCheckedChange = {
                                onValueChange(code.copy(usable = it))
                            })
                    }
                    Row {
                        Text(stringResource(R.string.used), modifier = Modifier.padding(10.dp))
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
                Button(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.updateCode()
                            if(viewModel.codesUiState.codesDetails.code.isNotEmpty()) {
                                onSaveEnd(viewModel.codesUiState.codesDetails.eventId)
                            } else {
                                navigateBack()
                            }
                        }
                    },
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.save_action))
                }
            }else{
                Text(text = stringResource(R.string.code_empty))
            }
        }
    }
}