package com.cambassador.app

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.cambassador.app.R.string
import com.cambassador.app.ui.Utility
import com.cambassador.app.ui.home.HomeDestination
import com.cambassador.app.ui.navigation.AmbassadorNavHost

@Composable
fun AmbassadorApp(navController: NavHostController = rememberNavController()) {
    AmbassadorNavHost(navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmbassadorTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit,
    menu: Boolean = false,
    url: String = ""
) {
    var onClickInfo by remember { mutableStateOf(false) }
    CenterAlignedTopAppBar(
        title = { Text(title) },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Filled.ArrowBack,
                        contentDescription = stringResource(string.back_button)
                    )
                }
            }
        },
        actions = { 
            if(menu){
                TopAppBarActions({ onClickInfo = true },{})
            }
        }
    )

    //QRcode
    if(onClickInfo && url.isNotBlank()){
        AlertDialog(
            onDismissRequest = {
                onClickInfo = false
            },
            title = {
                Text("URL")
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = Utility.qrCode(url),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(url)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClickInfo = false
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun TopAppBarActions(
    showDialog: () -> Unit,
    onMoreClick: () -> Unit
) {
    Row {
        IconButton(
            onClick = showDialog
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "More"
            )
        }
    }
}
