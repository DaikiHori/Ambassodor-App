package com.cambassador.app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.cambassador.app.R.string
import com.cambassador.app.ui.Utility
import com.cambassador.app.ui.navigation.AmbassadorNavHost
import com.google.rpc.Help

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
    navigateToUser: () -> Unit,
    menu: String = "",
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
            if(menu == "code"){
                CodeTopAppBarActions({ onClickInfo = true })
            }else if(menu == "home"){
                HomeTopAppBarActions({ onClickInfo = true })
            }
        }
    )

    //menu is code QRcode
    if(onClickInfo && url.isNotBlank()){
        QrCodeDialog(onDismissRequest = { onClickInfo = false }, url = url)
    }else if(onClickInfo && menu == "home"){
        MenuLinkDialog(
            onDismissRequest = { onClickInfo = false },
            onLinkClick = { navigateToUser() }
        )
    }
}

@Composable
fun CodeTopAppBarActions(
    showDialog: () -> Unit
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

@Composable
fun HomeTopAppBarActions(
    showDialog: () -> Unit,
){
    Row{
        IconButton(
            onClick = showDialog
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "More"
            )
        }
    }
}

@Composable
fun QrCodeDialog(
    onDismissRequest: () -> Unit,
    url: String,
){
    AlertDialog(
        onDismissRequest = onDismissRequest,
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
                onClick = onDismissRequest
            ) {
                Text("OK")
            }
        }
    )
}

@Composable
fun MenuLinkDialog(
    onDismissRequest: () -> Unit,
    onLinkClick: (String) -> Unit
){
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text("Menu")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.user_name),
                    color = Color.Blue,
                    modifier = Modifier.clickable { onLinkClick("user") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismissRequest
            ) {
                Text("Close")
            }
        },
        modifier = Modifier
            .clickable {
                onDismissRequest()
            }
    )
}