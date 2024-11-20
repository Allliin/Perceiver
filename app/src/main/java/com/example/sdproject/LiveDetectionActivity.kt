package com.example.sdproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
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
    private lateinit var touchIndicator: View // View for the white dot

    private lateinit var gestureDetector: GestureDetector

    companion object {
        const val PERMISSION_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_detection)

        // Initialize views
        previewView = findViewById(R.id.previewView)
        colorTextView = findViewById(R.id.colorTextView)
        colorImageView = findViewById(R.id.colorImageView)
        touchIndicator = findViewById(R.id.touchIndicator) // Initialize touch indicator

        // Make the activity full screen
        enableFullScreen()

        // Check camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), PERMISSION_CODE
            )
        }

        // Initialize GestureDetector
        gestureDetector = GestureDetector(this, SwipeGestureListener())

        // Set touch listener for the PreviewView
        previewView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event) // Pass the event to GestureDetector
            if (event.action == MotionEvent.ACTION_DOWN) {
                showTouchIndicator(event.x, event.y) // Show white dot on touch
                detectColorAt(event.x, event.y) // Detect color on single touch
            }
            true
        }
    }

    private fun enableFullScreen() {
        // Set the activity to full-screen mode
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageAnalysis = ImageAnalysis.Builder().build().also {
                // No specific analysis needed; we capture bitmap directly from PreviewView
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
                colorImageView.setBackgroundColor(pixelColor) // Update ImageView with the color
            } else {
                colorTextView.text = "Touch outside image bounds"
            }
        } else {
            // Handle cases where bitmap is null (e.g., no frame available)
            colorTextView.text = "No active preview available"
        }
    }

    private fun showTouchIndicator(x: Float, y: Float) {
        // Ensure the indicator is visible
        touchIndicator.visibility = View.VISIBLE

        // Get the layout params for ConstraintLayout
        val params = touchIndicator.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams

        // Update the position dynamically
        params.leftMargin = (x - touchIndicator.width / 2).toInt()
        params.topMargin = (y - touchIndicator.height / 2).toInt()

        // Apply the updated layout params
        touchIndicator.layoutParams = params

        // Animate fade-out after 2 seconds
        touchIndicator.animate()
            .alpha(0f)
            .setDuration(2000)
            .withEndAction {
                touchIndicator.visibility = View.GONE
                touchIndicator.alpha = 1f
            }
    }


    private fun allPermissionsGranted() = arrayOf(Manifest.permission.CAMERA).all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun navigateToMainMenu() {
        val intent = Intent(this, MainActivity::class.java) // Replace with your main menu activity class
        startActivity(intent)
        finish() // Optionally finish this activity to prevent returning to it
    }

    // Inner class for detecting swipe gestures
    inner class SwipeGestureListener : GestureDetector.SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (e1 == null || e2 == null) return false

            val diffX = e2.x - e1.x
            val diffY = e2.y - e1.y

            if (Math.abs(diffX) > Math.abs(diffY) &&
                Math.abs(diffX) > SWIPE_THRESHOLD &&
                Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD
            ) {
                if (diffX > 0) {
                    // Detected a right swipe
                    navigateToMainMenu()
                }
                return true
            }
            return false
        }
    }
}
