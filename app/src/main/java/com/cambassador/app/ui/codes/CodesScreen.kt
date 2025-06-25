package com.cambassador.app.ui.codes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import com.cambassador.app.ui.users.UsersViewModel
import kotlinx.coroutines.launch
import androidx.navigation.NavController

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
    codesViewModel: CodesViewModel = viewModel(factory = AppViewModelProvider.Factory),
    usersViewModel: UsersViewModel = viewModel(factory = AppViewModelProvider.Factory),
    menu: String = "code",
    modifier: Modifier = Modifier,
    navController: NavController
){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AmbassadorTopAppBar(
                title = stringResource(CodesDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp,
                scrollBehavior = scrollBehavior,
                menu = menu,
                url = codesViewModel.eventUiState.eventDetails.url,
                navController = navController
           )
        }
    ) { innerPadding ->
            CodesBody(
                codesUiState = codesViewModel.codesUiState,
                onValueChange = codesViewModel::updateUiState,
                navigateBack = navigateBack,
                codesViewModel = codesViewModel,
                modifier = modifier,
                contentPadding = innerPadding,
                onSaveEnd= onSaveEnd,
                navController = navController
            )
    }
}

@Composable
private fun CodesBody(
    codesUiState: CodesUiState,
    onValueChange: (CodesDetails) -> Unit,
    navigateBack: () -> Unit,
    codesViewModel: CodesViewModel,
    modifier: Modifier = Modifier,
    contentPadding : PaddingValues = PaddingValues(0.dp),
    onSaveEnd: (Int) -> Unit,
    navController: NavController
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var isExpanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var searchText by remember { mutableStateOf("") }
    val suggestionsList by codesViewModel.users.collectAsState(initial = emptyList())
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(contentPadding)
            .clickable { keyboardController?.hide() }
            .padding(10.dp),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            if(codesUiState.codesDetails.code.isNotEmpty()) {
                val code = codesUiState.codesDetails
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    LazyColumn(
                        modifier = modifier,
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
                    ) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .size(300.dp),
                                contentAlignment = Alignment.Center
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
                        }
                        item {
                            Spacer(modifier = Modifier.height(1.dp))
                        }
                        item {
                            Text(text=codesUiState.codesDetails.number.toString() + ":" + codesUiState.codesDetails.code, modifier = Modifier.padding(10.dp))
                        }
                        item {
                            Row {
                                Text(
                                    stringResource(R.string.usable),
                                    modifier = Modifier.padding(10.dp)
                                )
                                Switch(checked = code.usable,
                                    onCheckedChange = {
                                        onValueChange(code.copy(usable = it))
                                    })
                            }
                        }
                        item {
                            Row {
                                Text(
                                    stringResource(R.string.used),
                                    modifier = Modifier.padding(10.dp)
                                )
                                Switch(checked = code.used,
                                    onCheckedChange = {
                                        onValueChange(code.copy(used = it))
                                    })
                            }
                        }
                    }
                }
                LazyColumn {
                    if(suggestionsList != null) {
                        if (isExpanded && suggestionsList!!.isNotEmpty()) {
                            items(suggestionsList!!) { user ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = user.name,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onValueChange(code.copy(userName = user.name))
                                                isExpanded = false
                                            }
                                    )
                                }
                            }
                        }
                    }
                }
                OutlinedTextField(
                    value = code.userName,
                    onValueChange = {
                        onValueChange(code.copy(userName = it))
                        codesViewModel.searchUsers(it)
                        searchText = it
                        isExpanded = searchText.isNotEmpty()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = {
                        Text(stringResource(R.string.user_name))
                    },
                    label = { Text(stringResource(R.string.user_name)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        coroutineScope.launch {
                            codesViewModel.updateCode()
                            if (codesViewModel.codesUiState.codesDetails.code.isNotEmpty()) {
                                onSaveEnd(codesViewModel.codesUiState.codesDetails.eventId)
                            } else {
                                navigateBack()
                            }
                        }
                    },
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                ) {
                    Text(text = stringResource(R.string.save_action))
                }
            }else{
                Text(text = stringResource(R.string.code_empty))
            }
        }
    }
}