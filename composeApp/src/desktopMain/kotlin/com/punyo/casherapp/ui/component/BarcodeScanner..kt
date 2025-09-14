package com.punyo.casherapp.ui.component

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.platform.LocalDensity
import com.github.sarxos.webcam.Webcam
import com.github.sarxos.webcam.WebcamPanel
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.awt.image.BufferedImage
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.SwingUtilities

@Composable
fun BarcodeScanner(
    webcam: Webcam,
    onResult: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val panel = remember(webcam) {
        if (webcam.isOpen) webcam.close()
        webcam.apply {
            val sizes = viewSizes.toList()
            val maxByArea = sizes.maxByOrNull { it.width * it.height }
            if (maxByArea != null) {
                viewSize = maxByArea
            }
        }
        WebcamPanel(webcam, true).apply {
            preferredSize = webcam.viewSize
        }
    }
    DisposableEffect(webcam) {
        val scanning = AtomicBoolean(true)
        val reader = MultiFormatReader().apply {
            val hints = mapOf(
                DecodeHintType.TRY_HARDER to true,
                DecodeHintType.CHARACTER_SET to "UTF-8",
                DecodeHintType.POSSIBLE_FORMATS to listOf(
                    BarcodeFormat.QR_CODE,
                    BarcodeFormat.EAN_13,
                ),
            )

            setHints(hints)
        }

        fun centerCrop(src: BufferedImage, ratio: Double = 0.6): BufferedImage {
            val w = src.width
            val h = src.height
            val cw = (w * ratio).toInt().coerceAtLeast(1)
            val ch = (h * ratio).toInt().coerceAtLeast(1)
            val x = ((w - cw) / 2).coerceAtLeast(0)
            val y = ((h - ch) / 2).coerceAtLeast(0)
            return src.getSubimage(x, y, cw, ch)
        }

        fun tryDecode(image: BufferedImage): String? {
            centerCrop(image).let { roi ->
                try {
                    val source = BufferedImageLuminanceSource(roi)
                    val bitmap = BinaryBitmap(HybridBinarizer(source))
                    val result = reader.decodeWithState(bitmap)
                    if (result != null) return result.text
                } catch (_: NotFoundException) {
                }
            }
            return null
        }

        val scanThread = Thread {
            try {
                try {
                    if (!webcam.isOpen) webcam.open(true)
                } catch (_: Throwable) {
                    scanning.set(false)
                }
                while (scanning.get() && webcam.isOpen) {
                    val image = try {
                        webcam.image
                    } catch (_: Throwable) {
                        null
                    }
                    if (image != null) {
                        val text = tryDecode(image)
                        if (text != null) {
                            scanning.set(false)
                            SwingUtilities.invokeLater { onResult(text) }
                        }
                    }
                    try {
                        Thread.sleep(100)
                    } catch (_: InterruptedException) {
                        break
                    }
                }
            } finally {
                webcam.close()
            }
        }.apply { isDaemon = true }

        scanThread.start()

        onDispose {
            scanning.set(false)
            scanThread.interrupt()
            scanThread.join(250)
            webcam.close()
        }
    }

    val density = LocalDensity.current
    key(panel) {
        SwingPanel(
            factory = { panel },
            modifier = modifier.height(
                with(density) { webcam.viewSize.height.toDp() },
            ),
        )
    }
}
