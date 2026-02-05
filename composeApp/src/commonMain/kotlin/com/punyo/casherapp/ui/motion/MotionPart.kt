package com.punyo.casherapp.ui.motion

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color

/**
 * Represents a movable part in the motion playback system
 */
data class MotionPart(
    val id: String,
    val position: Offset,
    val size: Float,
    val color: Color = Color.Blue,
    val velocity: Offset = Offset.Zero,
    val isColliding: Boolean = false
) {
    /**
     * Get the bounding rectangle of this part
     */
    fun getBounds(): Rect {
        return Rect(
            left = position.x - size / 2,
            top = position.y - size / 2,
            right = position.x + size / 2,
            bottom = position.y + size / 2
        )
    }

    /**
     * Check if this part collides with another part
     */
    fun collidesWith(other: MotionPart): Boolean {
        val bounds = getBounds()
        val otherBounds = other.getBounds()
        return bounds.overlaps(otherBounds)
    }

    /**
     * Update position based on velocity
     */
    fun updatePosition(deltaTime: Float): MotionPart {
        return copy(
            position = Offset(
                x = position.x + velocity.x * deltaTime,
                y = position.y + velocity.y * deltaTime
            )
        )
    }
}
