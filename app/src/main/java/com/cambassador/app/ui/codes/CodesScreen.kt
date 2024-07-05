package com.cambassador.app.ui.codes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.cambassador.app.AmbassadorTopAppBar
import com.cambassador.app.R
import com.cambassador.app.data.User
import com.cambassador.app.ui.AppViewModelProvider
import com.cambassador.app.ui.Utility
import com.cambassador.app.ui.navigation.NavigationDestination
import com.cambassador.app.ui.users.UsersViewModel
import kotlinx.coroutines.flow.StateFlow
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
    codesViewModel: CodesViewModel = viewModel(factory = AppViewModelProvider.Factory),
    usersViewModel: UsersViewModel = viewModel(factory = AppViewModelProvider.Factory),
    menu: String = "code",
    modifier: Modifier = Modifier
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
                navigateToUser = {},
                url = codesViewModel.eventUiState.eventDetails.url
           )
        }
    ) { innerPadding ->
            CodesBody(
                codesUiState = codesViewModel.codesUiState,
                onValueChange = codesViewModel::updateUiState,
                navigateBack = navigateBack,
                codesViewModel = codesViewModel,
                usersViewModel = usersViewModel,
                modifier = modifier,
                contentPadding = innerPadding,
                onSaveEnd= onSaveEnd
            )
    }
}

@Composable
private fun CodesBody(
    codesUiState: CodesUiState,
    onValueChange: (CodesDetails) -> Unit,
    navigateBack: () -> Unit,
    codesViewModel: CodesViewModel,
    usersViewModel: UsersViewModel,
    modifier: Modifier = Modifier,
    contentPadding : PaddingValues = PaddingValues(0.dp),
    onSaveEnd: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .imePadding()
            .padding(contentPadding)
            .padding(10.dp),
    ) {
        val coroutineScope = rememberCoroutineScope()
        var searchText by remember { mutableStateOf("") }

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
                    AutoCompleteTextField(
                        value = searchText,
                        onValueChange = { text ->
                            searchText = text
                            codesViewModel.searchUsers(text)
                        },
                        users = codesViewModel.users,
                        onSuggestionSelected = { user ->
                            searchText = user.name
                            onValueChange(code.copy(userName = searchText))
                        }
                    )
                }
                Button(
                    onClick = {
                        coroutineScope.launch {
                            codesViewModel.updateCode()
                            if(codesViewModel.codesUiState.codesDetails.code.isNotEmpty()) {
                                onSaveEnd(codesViewModel.codesUiState.codesDetails.eventId)
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

@Composable
fun AutoCompleteTextField(
    value: String,
    onValueChange: (String) -> Unit,
    users: StateFlow<List<User>?>,
    onSuggestionSelected: (User) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var isExpanded by remember { mutableStateOf(false) }
    val suggestionsList by users.collectAsState(initial = emptyList())
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
    ) {
        Column {
            if (isExpanded && suggestionsList != null && suggestionsList!!.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.White)
                        .padding(16.dp)
                        .clickable { keyboardController?.hide() }
                ) {
                    LazyColumn {
                        items(suggestionsList!!) { user ->
                            Text(
                                text = user.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSuggestionSelected(user)
                                        isExpanded = false
                                    }
                                    .padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
            OutlinedTextField(
                value = value,
                onValueChange = { text ->
                    onValueChange(text)
                    isExpanded = text.isNotEmpty()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
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
        }
    }
}
