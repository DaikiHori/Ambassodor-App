package com.cambassador.app.ui.users

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cambassador.app.AmbassadorTopAppBar
import com.cambassador.app.R
import com.cambassador.app.ui.AppViewModelProvider
import com.cambassador.app.ui.navigation.NavigationDestination
import com.cambassador.app.data.User

object UsersDestination : NavigationDestination {
    override val route = "users"
    override val titleRes = R.string.user_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(
    navigateToUserEntry: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UsersViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onNavigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    menu: String = "user"
){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val users by viewModel.displayUsers.collectAsState()
    Scaffold(
        topBar = {
            AmbassadorTopAppBar(
                title = stringResource(UsersDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp,
                menu = menu,
                navigateToUser = {}
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToUserEntry,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .padding(
                        end = WindowInsets.safeDrawing.asPaddingValues()
                            .calculateEndPadding(LocalLayoutDirection.current)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.event_entry_title)
                )
            }
        }
    ){ innerPadding ->
            UsersBody(
                userList = users,
                viewModel = viewModel,
                modifier = modifier.fillMaxSize(),
                contentPadding = innerPadding
            )
    }
}

@Composable
fun UsersBody(
    userList: List<User>?,
    viewModel: UsersViewModel,
    modifier: Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(contentPadding)
    ) {
        if (userList != null && userList.isEmpty()) {
            Text(
                text = stringResource(R.string.users_empty),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding),
            )
        } else {
            LazyColumn(
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small)),
                contentPadding = PaddingValues(5.dp, 50.dp, 5.dp, 5.dp)
            ) {
                userList?.let { data ->
                    itemsIndexed(data, key = { _, item -> item.id }) { index, it ->
                        val listIndex = index + 1
                        Card(
                            modifier = modifier,
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(modifier = Modifier.padding(5.dp)) {
                                Text(text = "$listIndex: ")
                                SelectionContainer {
                                    Text(text = it.name)
                                }
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}