/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.inventory.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.inventory.ui.codes.CodesDestination
import com.example.inventory.ui.codes.CodesScreen
import com.example.inventory.ui.home.HomeDestination
import com.example.inventory.ui.home.HomeScreen
import com.example.inventory.ui.event.EventDetailsDestination
import com.example.inventory.ui.event.EventDetailsScreen
import com.example.inventory.ui.event.EventEditDestination
import com.example.inventory.ui.event.EventEditScreen
import com.example.inventory.ui.event.EventEntryDestination
import com.example.inventory.ui.event.EventEntryScreen

/**
 * Provides Navigation graph for the application.
 */
@Composable
fun InventoryNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToEventEntry = { navController.navigate(EventEntryDestination.route) },
                navigateToCodes = { navController.navigate(CodesDestination.route) }
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
                navigateToCodes = { navController.navigate("${CodesDestination.route}/$it") },
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
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}
