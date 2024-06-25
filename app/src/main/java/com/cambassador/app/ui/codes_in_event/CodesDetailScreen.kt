package com.cambassador.app.ui.codes_in_event

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cambassador.app.AmbassadorTopAppBar
import com.cambassador.app.R
import com.cambassador.app.data.Code
import com.cambassador.app.data.Event
import com.cambassador.app.ui.AppViewModelProvider
import com.cambassador.app.ui.event.toEvent
import com.cambassador.app.ui.navigation.NavigationDestination

object CodesDetailsDestination : NavigationDestination {
    override val route = "codes_details"
    override val titleRes = R.string.code_long
    const val eventIdArg = "eventId"
    val routeWithArgs = "$route/{$eventIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodesDetailsScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CodesDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onNavigateUp: () -> Boolean
){
    val codesUiState by viewModel.codesUiState.collectAsState()
    val eventUiState by viewModel.eventUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AmbassadorTopAppBar(
                title = stringResource(CodesDetailsDestination.titleRes),
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = navigateBack
            )
        },
    ) { innerPadding ->
        CodesBody(
            event = eventUiState.eventDetails.toEvent(),
            codeList = codesUiState.codeList,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodesBody(
    event: Event,
    codeList: List<Code>,
    modifier: Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
){
    Column(modifier = Modifier.padding(contentPadding)) {
        Text(text = event.name, modifier = Modifier.padding(5.dp).height(25.dp))
        Divider(color = Color.Gray)
        LazyColumn(
            modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small)),
            contentPadding = PaddingValues(5.dp,50.dp,5.dp,5.dp)
        ) {
            items(items = codeList, key = { it.id }) {
                Card(
                    modifier = modifier,
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
                    ) {
                        Row() {
                            Text(text = it.code)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = if(it.usable){
                                stringResource(R.string.usable)}else{
                                stringResource(R.string.usable_false)})
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = if(it.used){
                                stringResource(R.string.used)}else{
                                stringResource(R.string.used_false)})
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(5.dp))
            }
        }
    }
}