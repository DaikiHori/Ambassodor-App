package com.cambassador.app.ui.codes_in_event

import com.cambassador.app.R
import com.cambassador.app.ui.navigation.NavigationDestination

object CodesDetailsDestination : NavigationDestination {
    override val route = "codes_details"
    override val titleRes = R.string.codes
    const val eventIdArg = "eventId"
    val routeWithArgs = "$route/{$eventIdArg}"
}