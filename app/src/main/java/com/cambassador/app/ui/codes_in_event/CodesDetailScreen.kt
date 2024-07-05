package com.cambassador.app.ui.codes_in_event

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
    val codes by viewModel.displayCodes.collectAsState()
    val eventUiState by viewModel.eventUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AmbassadorTopAppBar(
                title = stringResource(CodesDetailsDestination.titleRes),
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = navigateBack,
                navigateToUser = {}
            )
        },
    ) { innerPadding ->
        CodesBody(
            event = eventUiState.eventDetails.toEvent(),
            codeList = codes,
            viewModel = viewModel,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodesBody(
    event: Event,
    codeList: List<Code>?,
    viewModel: CodesDetailsViewModel,
    modifier: Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
){
    Column(modifier = Modifier.padding(contentPadding)) {
        Text(
            text = event.name, modifier = Modifier
                .padding(5.dp)
                .height(25.dp)
        )
        Divider(color = Color.Gray)
        LazyColumn(
            modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small)),
            contentPadding = PaddingValues(5.dp,50.dp,5.dp,5.dp)
        ) {
            codeList?.let { data ->
                itemsIndexed(data, key = { _, item -> item.id }) { index, it ->
                    val listIndex = index + 1
                    Card(
                        modifier = modifier,
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
                            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
                        ) {
                            Column() {
                                Row {
                                    Text(text = "$listIndex: ")
                                    SelectionContainer {
                                        Text(text = it.code)
                                    }
                                }
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Row(modifier = Modifier.weight(0.5f)) {
                                        Checkbox(
                                            checked = it.usable,
                                            onCheckedChange = { isChecked ->
                                                viewModel.updateCode(
                                                    it.id,
                                                    it.copy(usable = isChecked)
                                                )
                                                viewModel.saveCode(it.id)
                                            }
                                        )
                                        Text(
                                            text = if (it.usable) {
                                                stringResource(R.string.usable)
                                            } else {
                                                stringResource(R.string.usable_false)
                                            },
                                            modifier = Modifier.align(Alignment.CenterVertically)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Row(modifier = Modifier.weight(0.5f)) {
                                        Checkbox(
                                            checked = it.used,
                                            onCheckedChange = { isChecked ->
                                                viewModel.updateCode(
                                                    it.id,
                                                    it.copy(used = isChecked)
                                                )
                                                viewModel.saveCode(it.id)
                                            }
                                        )
                                        Text(
                                            text = if (it.used) {
                                                stringResource(R.string.used)
                                            } else {
                                                stringResource(R.string.used_false)
                                            },
                                            modifier = Modifier.align(Alignment.CenterVertically)
                                        )
                                    }
                                }
                                Text(text = it.userName)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.padding(5.dp))
                }
            }
        }
    }
}