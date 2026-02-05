package com.punyo.casherapp.ui.motion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel for managing motion playback with collision detection
 */
class MotionPlaybackViewModel : ViewModel() {
    var state by mutableStateOf(MotionPlaybackState())
        private set

    private var playbackJob: Job? = null
    private val frameDelay = 16L // ~60 FPS
    private val deltaTime = frameDelay / 1000f

    init {
        // Initialize with sample parts for demonstration
        initializeSampleParts()
    }

    /**
     * Initialize sample parts with different velocities
     */
    private fun initializeSampleParts() {
        state = MotionPlaybackState(
            parts = listOf(
                MotionPart(
                    id = "part1",
                    position = Offset(100f, 100f),
                    size = 50f,
                    color = Color.Blue,
                    velocity = Offset(50f, 30f)
                ),
                MotionPart(
                    id = "part2",
                    position = Offset(400f, 200f),
                    size = 50f,
                    color = Color.Green,
                    velocity = Offset(-40f, 20f)
                ),
                MotionPart(
                    id = "part3",
                    position = Offset(250f, 350f),
                    size = 50f,
                    color = Color.Magenta,
                    velocity = Offset(30f, -50f)
                )
            )
        )
    }

    /**
     * Start motion playback
     */
    fun startPlayback() {
        if (state.hasCollision) {
            // Reset if collision already occurred
            resetPlayback()
        }

        state = state.copy(isPlaying = true)
        
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            while (state.isPlaying) {
                delay(frameDelay)
                updateFrame()
            }
        }
    }

    /**
     * Stop motion playback
     */
    fun stopPlayback() {
        state = state.copy(isPlaying = false)
        playbackJob?.cancel()
    }

    /**
     * Reset playback to initial state
     */
    fun resetPlayback() {
        stopPlayback()
        initializeSampleParts()
    }

    /**
     * Update a single frame of animation
     */
    private fun updateFrame() {
        // Update positions
        state = state.updatePositions(deltaTime)
        
        // Detect collisions
        state = state.detectCollisions()
        
        // If collision detected, playback is automatically stopped in detectCollisions()
    }
}
