package com.punyo.casherapp.ui.motion

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

const val MOTION_PLAYBACK_ROUTE = "motion_playback_route"

fun NavGraphBuilder.motionPlaybackScreen(
    onNavigateBack: () -> Unit
) {
    composable(MOTION_PLAYBACK_ROUTE) {
        MotionPlaybackScreen(onNavigateBack = onNavigateBack)
    }
}

fun NavHostController.navigateToMotionPlayback() {
    navigate(MOTION_PLAYBACK_ROUTE)
}
