package dev.jyotiraditya.echoir.presentation.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import dev.jyotiraditya.echoir.domain.model.SearchResult
import dev.jyotiraditya.echoir.presentation.navigation.Route
import dev.jyotiraditya.echoir.presentation.navigation.components.EchoirBottomNav
import dev.jyotiraditya.echoir.presentation.navigation.components.EchoirTopBar
import dev.jyotiraditya.echoir.presentation.screens.details.DetailsScreen
import dev.jyotiraditya.echoir.presentation.screens.home.HomeScreen
import dev.jyotiraditya.echoir.presentation.screens.search.SearchScreen
import dev.jyotiraditya.echoir.presentation.screens.settings.SettingsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route?.let { Route.fromPath(it) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            EchoirTopBar(
                currentRoute = currentRoute,
                onNavigateBack = { navController.popBackStack() },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            EchoirBottomNav(
                navController = navController,
                currentRoute = currentRoute
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Home.path,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Route.Home.path) {
                HomeScreen()
            }
            composable(Route.Search.Main.path) {
                SearchScreen(navController)
            }
            composable(
                route = Route.Search.Details().createRoute(),
                arguments = listOf(
                    navArgument(Route.Search.Details.TYPE_ARG) { type = NavType.StringType },
                    navArgument(Route.Search.Details.ID_ARG) { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val type = backStackEntry.arguments?.getString(Route.Search.Details.TYPE_ARG)
                val id = backStackEntry.arguments?.getLong(Route.Search.Details.ID_ARG)
                val result = navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<SearchResult>("result")

                if (type != null && id != null && result != null) {
                    DetailsScreen(result)
                }
            }
            composable(Route.Settings.path) {
                SettingsScreen()
            }
        }
    }
}
