package com.cambassador.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cambassador.app.ui.codes.CodesDestination
import com.cambassador.app.ui.codes.CodesScreen
import com.cambassador.app.ui.codes_in_event.CodesDetailsDestination
import com.cambassador.app.ui.codes_in_event.CodesDetailsScreen
import com.cambassador.app.ui.home.HomeDestination
import com.cambassador.app.ui.home.HomeScreen
import com.cambassador.app.ui.event.EventDetailsDestination
import com.cambassador.app.ui.event.EventDetailsScreen
import com.cambassador.app.ui.event.EventEditDestination
import com.cambassador.app.ui.event.EventEditScreen
import com.cambassador.app.ui.event.EventEntryDestination
import com.cambassador.app.ui.event.EventEntryScreen
import com.cambassador.app.ui.users.UserDetails
import com.cambassador.app.ui.users.UserEntryDestination
import com.cambassador.app.ui.users.UserEntryScreen
import com.cambassador.app.ui.users.UsersDestination
import com.cambassador.app.ui.users.UsersScreen

@Composable
fun AmbassadorNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(
            route = HomeDestination.route,
            arguments = listOf(navArgument(CodesDestination.eventIdArg) {
                type = NavType.IntType
            })
        ) {
            HomeScreen(
                navigateToEventEntry = { navController.navigate(EventEntryDestination.route) },
                navigateToEventEdit = { navController.navigate("${EventEditDestination.route}/$it")},
                navigateToCodes = { navController.navigate("${CodesDestination.route}/$it")},
                navigateToCodesDetails = { navController.navigate("${CodesDetailsDestination.route}/$it")},
                navigateToUsers = { navController.navigate(UsersDestination.route) },
                onNavigateBack = { navController.popBackStack(HomeDestination.route,inclusive = false) }
            )
        }

        composable(route = EventEntryDestination.route) {
            EventEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = EventDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(EventDetailsDestination.eventIdArg) {
                type = NavType.IntType
            })
        ) {
            EventDetailsScreen(
                navigateToCodes = { navController.navigate("${CodesDestination.routeWithArgs}/$it") },
                navigateToEditEvent = { navController.navigate("${EventEditDestination.route}/$it") },
                navigateBack = { navController.navigateUp() }
            )
        }

        composable(
            route = EventEditDestination.routeWithArgs,
            arguments = listOf(navArgument(EventEditDestination.eventIdArg) {
                type = NavType.IntType
            })
        ) {
            EventEditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = CodesDestination.routeWithArgs,
            arguments = listOf(navArgument(CodesDestination.eventIdArg) {
                type = NavType.IntType
            })
        ) {
            CodesScreen(
                navigateBack = { navController.popBackStack(HomeDestination.route,inclusive = false) },
                onNavigateUp = { navController.popBackStack(HomeDestination.route,inclusive = false) },
                onSaveEnd =  { navController.navigate("${CodesDestination.route}/$it")}
            )
        }

        composable(
            route = CodesDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(CodesDetailsDestination.eventIdArg) {
                type = NavType.IntType
            })
        ) {
            CodesDetailsScreen(
                navigateBack = { navController.popBackStack(HomeDestination.route,inclusive = false) },
                onNavigateUp = { navController.popBackStack(HomeDestination.route,inclusive = false) }
            )
        }

        composable(route = UsersDestination.route){
            UsersScreen(
                navigateToUserEntry = { navController.navigate(UserEntryDestination.route) },
                onNavigateUp = { navController.navigateUp() },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = UserEntryDestination.route){
            UserEntryScreen(
                navigateBack = { navController.navigateUp() },
                onNavigateUp = { navController.popBackStack() }
            )
        }
    }
}
