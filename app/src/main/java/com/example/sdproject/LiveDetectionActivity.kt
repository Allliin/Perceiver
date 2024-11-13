package com.example.sdproject

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class LiveDetectionActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var colorTextView: TextView
    private lateinit var colorImageView: ImageView

    companion object {
        const val PERMISSION_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_detection)

        previewView = findViewById(R.id.previewView)
        colorTextView = findViewById(R.id.colorTextView)
        colorImageView = findViewById(R.id.colorImageView)

        // Check for camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), PERMISSION_CODE
            )
        }

        // Set touch listener on PreviewView to detect color at touch point
        previewView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val touchX = event.x
                val touchY = event.y
                detectColorAt(touchX, touchY)
            }
            true
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageAnalysis = ImageAnalysis.Builder().build().also {
                // No specific analysis function needed; we are capturing bitmap directly from PreviewView
                it.setAnalyzer(ContextCompat.getMainExecutor(this)) { imageProxy ->
                    imageProxy.close()
                }
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Bind the camera to the lifecycle
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun detectColorAt(x: Float, y: Float) {
        // Capture the current frame as a bitmap from PreviewView
        val bitmap: Bitmap? = previewView.bitmap

        if (bitmap != null) {
            // Convert touch coordinates to bitmap scale
            val bitmapX = (x / previewView.width) * bitmap.width
            val bitmapY = (y / previewView.height) * bitmap.height

            // Ensure coordinates are within bitmap bounds
            if (bitmapX >= 0 && bitmapX < bitmap.width && bitmapY >= 0 && bitmapY < bitmap.height) {
                val pixelColor = bitmap.getPixel(bitmapX.toInt(), bitmapY.toInt())

                // Convert color to hex and get color name
                val hexColor = String.format("#%06X", 0xFFFFFF and pixelColor)
                val colorName = ColorDatabase.getClosestColor(hexColor)

                // Update TextView with color info
                colorTextView.text = "Color: $colorName ($hexColor)"

                // Update ImageView with the detected color
                colorImageView.setBackgroundColor(pixelColor)  // Update ImageView with the color
            } else {
                colorTextView.text = "Touch outside image bounds"
            }
        } else {
            // Handle cases where bitmap is null (e.g., no frame available)
            colorTextView.text = "No active preview available"
        }
    }

    private fun allPermissionsGranted() = arrayOf(Manifest.permission.CAMERA).all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
}
