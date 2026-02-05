package com.punyo.casherapp.ui.motion

/**
 * Represents the state of motion playback
 */
data class MotionPlaybackState(
    val parts: List<MotionPart> = emptyList(),
    val isPlaying: Boolean = false,
    val hasCollision: Boolean = false,
    val collidingPartIds: Set<String> = emptySet(),
    val elapsedTime: Float = 0f
) {
    /**
     * Detect collisions between all parts
     */
    fun detectCollisions(): MotionPlaybackState {
        val collidingIds = mutableSetOf<String>()
        
        for (i in parts.indices) {
            for (j in i + 1 until parts.size) {
                if (parts[i].collidesWith(parts[j])) {
                    collidingIds.add(parts[i].id)
                    collidingIds.add(parts[j].id)
                }
            }
        }
        
        val hasCollision = collidingIds.isNotEmpty()
        val updatedParts = parts.map { part ->
            part.copy(isColliding = collidingIds.contains(part.id))
        }
        
        return copy(
            parts = updatedParts,
            hasCollision = hasCollision,
            collidingPartIds = collidingIds,
            isPlaying = if (hasCollision) false else isPlaying // Stop playback on collision
        )
    }

    /**
     * Update all parts positions
     */
    fun updatePositions(deltaTime: Float): MotionPlaybackState {
        val updatedParts = parts.map { it.updatePosition(deltaTime) }
        return copy(
            parts = updatedParts,
            elapsedTime = elapsedTime + deltaTime
        )
    }
}
