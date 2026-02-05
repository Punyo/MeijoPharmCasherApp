package com.punyo.casherapp.ui.motion

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Screen for demonstrating motion playback with collision detection
 */
@Composable
fun MotionPlaybackScreen(
    onNavigateBack: () -> Unit,
    viewModel: MotionPlaybackViewModel = viewModel { MotionPlaybackViewModel() }
) {
    val state = viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Motion Playback Demo") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Text("←")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Canvas for drawing parts
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 16.dp)
            ) {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    state.parts.forEach { part ->
                        val drawColor = if (part.isColliding) {
                            Color.Red // Highlight colliding parts in red
                        } else {
                            part.color
                        }
                        
                        drawCircle(
                            color = drawColor,
                            radius = part.size,
                            center = part.position
                        )
                    }
                }
            }

            // Status information
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Status: ${if (state.isPlaying) "Playing" else "Stopped"}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    if (state.hasCollision) {
                        Text(
                            text = "⚠️ Collision Detected!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Red
                        )
                        Text(
                            text = "Colliding parts: ${state.collidingPartIds.joinToString(", ")}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
                    Text(
                        text = "Elapsed time: ${"%.2f".format(state.elapsedTime)}s",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Control buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.startPlayback() },
                    modifier = Modifier.weight(1f),
                    enabled = !state.isPlaying
                ) {
                    Text(if (state.hasCollision) "Restart" else "Play")
                }
                
                Button(
                    onClick = { viewModel.stopPlayback() },
                    modifier = Modifier.weight(1f),
                    enabled = state.isPlaying
                ) {
                    Text("Stop")
                }
                
                Button(
                    onClick = { viewModel.resetPlayback() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reset")
                }
            }
        }
    }
}
