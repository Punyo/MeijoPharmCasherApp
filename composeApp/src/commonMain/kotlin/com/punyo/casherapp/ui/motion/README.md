# Motion Playback with Collision Detection

## Overview

This feature adds a motion playback demonstration system with real-time collision detection and visual feedback.

## Features

1. **Animated Parts**: Multiple parts (circles) move across the canvas with defined velocities
2. **Collision Detection**: Real-time detection when parts overlap during motion playback
3. **Automatic Stop**: Motion playback automatically stops when a collision is detected
4. **Visual Highlighting**: Colliding parts are highlighted in red color
5. **Playback Controls**: Play, Stop, and Reset buttons for controlling the animation

## Implementation Details

### Core Components

- **MotionPart.kt**: Data class representing a movable part with position, size, velocity, and collision state
- **MotionPlaybackState.kt**: State manager for the entire playback system, handles collision detection
- **MotionPlaybackViewModel.kt**: ViewModel that manages the animation loop and state updates
- **MotionPlaybackScreen.kt**: Compose UI screen displaying the canvas and controls
- **MotionPlaybackNavigation.kt**: Navigation integration for the feature

### How It Works

1. Parts are initialized with random positions and velocities
2. Animation updates positions at ~60 FPS
3. After each frame, collision detection runs:
   - Checks for overlapping bounding rectangles
   - Marks colliding parts
   - Stops playback if collision detected
4. UI updates to show:
   - Parts in their original colors (normal state)
   - Parts in red (collision state)
   - Status information and elapsed time

### Accessing the Feature

Navigate to **Settings → Motion Playback Demo** to access the feature.

## Technical Notes

- Uses Jetpack Compose Canvas for rendering
- Implements frame-by-frame animation using coroutines
- Collision detection uses AABB (Axis-Aligned Bounding Box) algorithm
- State management follows unidirectional data flow pattern
