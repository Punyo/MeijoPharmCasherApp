package com.punyo.casherapp.data.camera.source

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.flow.Flow

expect class CameraLocalDataSource {
    suspend fun startCameraPreview(cameraIndex: Int = 0): Flow<ImageBitmap>
    suspend fun getAvailableCameraCount(): Int
    fun stopCamera()
    val isCameraInitialized: Boolean
}
