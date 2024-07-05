package com.cambassador.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cambassador.app.AmbassadorTopAppBar
import com.cambassador.app.R
import com.cambassador.app.data.EventAndCodes
import com.cambassador.app.ui.AppViewModelProvider
import com.cambassador.app.ui.Utility
import com.cambassador.app.ui.navigation.NavigationDestination

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToEventEntry: () -> Unit,
    navigateToEventEdit: (Int) -> Unit,
    navigateToCodes: (Int) -> Unit,
    navigateToCodesDetails: (Int) -> Unit,
    navigateToUsers: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onNavigateBack: () -> Unit
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AmbassadorTopAppBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior,
                navigateUp = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToEventEntry,
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
        },
    ) { innerPadding ->
        HomeBody(
            eventList = homeUiState.eventList,
            navigateToCodes = navigateToCodes,
            navigateToEventEdit = navigateToEventEdit,
            navigateToCodesDetails = navigateToCodesDetails,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding
        )
    }
}

@Composable
private fun HomeBody(
    eventList: List<EventAndCodes>,
    navigateToCodes: (Int) -> Unit,
    navigateToEventEdit: (Int) -> Unit,
    navigateToCodesDetails: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        if (eventList.isEmpty()) {
            Text(
                text = stringResource(R.string.no_event_description),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding),
            )
        } else {
            EventList(
                eventList = eventList,
                navigateToCodes = navigateToCodes ,
                navigateToEventEdit = navigateToEventEdit,
                navigateToCodesDetails = navigateToCodesDetails,
                contentPadding = contentPadding,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

@Composable
private fun EventList(
    eventList: List<EventAndCodes>,
    navigateToCodes: (Int) -> Unit,
    navigateToEventEdit: (Int) -> Unit,
    navigateToCodesDetails: (Int) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(items = eventList, key = { it.event.id }) { event ->
            Card(
                modifier = modifier, elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = event.event.name,
                                style = MaterialTheme.typography.titleLarge,
                            )
                            Spacer(Modifier.weight(1f))
                            Text(
                                text = Utility.dateToString(event.event.date),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = event.codes.count{ !it.used && it.usable }.toString() + "/" + event.codes.count().toString(),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Row {
                        Button(
                            onClick = { navigateToEventEdit(event.event.id) },
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(text = stringResource(R.string.edit_action))
                        }
                        Spacer(Modifier.weight(1f))
                        Button(
                            onClick = { navigateToCodesDetails(event.event.id) },
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(text = stringResource(R.string.list))
                        }
                        Spacer(Modifier.weight(1f))
                        Button(
                            onClick = { navigateToCodes(event.event.id) },
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(text = stringResource(R.string.code))
                        }
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}