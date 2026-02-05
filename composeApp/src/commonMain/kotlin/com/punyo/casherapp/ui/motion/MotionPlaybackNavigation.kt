package com.punyo.casherapp.ui.motion

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.punyo.casherapp.ui.navigation.NavigationDestinations

fun NavGraphBuilder.motionPlaybackScreen(
    onNavigateBack: () -> Unit
) {
    composable(NavigationDestinations.MOTION_PLAYBACK_ROUTE) {
        MotionPlaybackScreen(onNavigateBack = onNavigateBack)
    }
}

fun NavHostController.navigateToMotionPlayback() {
    navigate(NavigationDestinations.MOTION_PLAYBACK_ROUTE)
}
