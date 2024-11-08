package com.example.sdproject

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.InputStream

class GalleryDetectionActivity : AppCompatActivity() {

    private lateinit var selectImageButton: Button
    private lateinit var colorTextView: TextView
    private lateinit var imageView: ImageView
    private lateinit var photoPickerLauncher: ActivityResultLauncher<Intent>
    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery_detection)

        colorTextView = findViewById(R.id.colorTextView)
        selectImageButton = findViewById(R.id.selectImageButton)
        imageView = findViewById(R.id.imageView) // Assuming you have an ImageView in the layout

        // Initialize the photo picker launcher for gallery image selection
        photoPickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { uri ->
                    bitmap = loadBitmapFromUri(uri)
                    bitmap?.let {
                        imageView.setImageBitmap(it)
                    }
                }
            }
        }

        // Set up button click listener to select an image from the gallery
        selectImageButton.setOnClickListener {
            selectImageFromGallery()
        }
        // Function to get adjusted bitmap coordinates based on ImageView scaling
        fun getBitmapCoordinates(touchX: Float, touchY: Float): Pair<Int, Int>? {
            // Ensure the ImageView and Bitmap are properly initialized
            if (imageView.drawable == null || bitmap == null) return null

            // Calculate the scale factor and position offset for the image inside the ImageView
            val scaleX = imageView.width.toFloat() / bitmap!!.width
            val scaleY = imageView.height.toFloat() / bitmap!!.height
            val scaleFactor = scaleX.coerceAtMost(scaleY)

            // Calculate offset if ImageView dimensions differ from bitmap dimensions after scaling
            val offsetX = (imageView.width - bitmap!!.width * scaleFactor) / 2
            val offsetY = (imageView.height - bitmap!!.height * scaleFactor) / 2

            // Convert the touch coordinates to the bitmap's coordinate system
            val adjustedX = ((touchX - offsetX) / scaleFactor).toInt()
            val adjustedY = ((touchY - offsetY) / scaleFactor).toInt()

            // Ensure the adjusted coordinates are within the bitmap bounds
            return if (adjustedX in 0 until bitmap!!.width && adjustedY in 0 until bitmap!!.height) {
                Pair(adjustedX, adjustedY)
            } else {
                null
            }
        }

        // Handle touch events on the image to detect the color
        imageView.setOnTouchListener { _, event ->
            if (bitmap != null && event.action == MotionEvent.ACTION_DOWN) {
                // Get adjusted touch coordinates
                val adjustedPoint = getBitmapCoordinates(event.x, event.y)

                if (adjustedPoint != null) {
                    val (x, y) = adjustedPoint

                    // Get the pixel color at the adjusted coordinates
                    val color = bitmap!!.getPixel(x, y)
                    val hexColor = String.format("#%06X", (0xFFFFFF and color))
                    val colorName = ColorDatabase.getClosestColor(hexColor)

                    // Update the color text view
                    colorTextView.text = "Color: $colorName ($hexColor)"

                    // Mark the touch position on the image
                    val markedBitmap = markTouchPosition(bitmap!!, x, y)
                    imageView.setImageBitmap(markedBitmap)
                }
            }
            true
        }


    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        photoPickerLauncher.launch(intent)
    }

    private fun loadBitmapFromUri(uri: Uri): Bitmap? {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        return BitmapFactory.decodeStream(inputStream)
    }

    private fun markTouchPosition(bitmap: Bitmap, x: Int, y: Int): Bitmap {
        val markedBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true) // Copy the bitmap so we can modify it
        val canvas = Canvas(markedBitmap)

        // Draw a marker (red circle) at the touch location
        val paint = Paint().apply {
            color = Color.RED
            style = Paint.Style.FILL
            strokeWidth = 10f
        }
        canvas.drawCircle(x.toFloat(), y.toFloat(), 20f, paint) // Draw a circle with radius 20px

        return markedBitmap
    }
}
