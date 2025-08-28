package com.punyo.casherapp.data.camera.source

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.flow.Flow

actual class CameraLocalDataSource {
    actual suspend fun startCameraPreview(cameraIndex: Int): Flow<ImageBitmap> {
        throw NotImplementedError("Camera is not supported on Desktop")
        return TODO("戻り値の提供")
    }
    actual suspend fun getAvailableCameraCount(): Int = 0
    actual fun stopCamera() {
    }
    actual val isCameraInitialized: Boolean
}
