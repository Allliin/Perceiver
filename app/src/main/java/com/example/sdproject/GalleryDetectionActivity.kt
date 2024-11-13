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
    private lateinit var colorImageView: ImageView // Color preview view
    private lateinit var photoPickerLauncher: ActivityResultLauncher<Intent>
    private var originalBitmap: Bitmap? = null // Store original bitmap separately
    private var displayedBitmap: Bitmap? = null // Bitmap with markings to display

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery_detection)

        colorTextView = findViewById(R.id.colorTextView)
        selectImageButton = findViewById(R.id.selectImageButton)
        imageView = findViewById(R.id.imageView)
        colorImageView = findViewById(R.id.colorImageView) // Initialize color preview ImageView

        // Initialize photo picker launcher
        photoPickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { uri ->
                    originalBitmap = loadBitmapFromUri(uri)
                    originalBitmap?.let {
                        displayedBitmap = it.copy(Bitmap.Config.ARGB_8888, true) // Copy for displaying
                        imageView.setImageBitmap(displayedBitmap)
                    }
                }
            }
        }

        // Set up button click listener to select an image from the gallery
        selectImageButton.setOnClickListener {
            selectImageFromGallery()
        }

        // Handle touch events on the image to detect color
        imageView.setOnTouchListener { _, event ->
            if (originalBitmap != null && event.action == MotionEvent.ACTION_DOWN) {
                val adjustedPoint = getBitmapCoordinates(event.x, event.y)
                if (adjustedPoint != null) {
                    val (x, y) = adjustedPoint

                    // Get pixel color and display it
                    val color = originalBitmap!!.getPixel(x, y)
                    val hexColor = String.format("#%06X", (0xFFFFFF and color))
                    val colorName = ColorDatabase.getClosestColor(hexColor)

                    colorTextView.text = "Color: $colorName ($hexColor)"
                    colorImageView.setBackgroundColor(color) // Update color preview

                    // Update marked image
                    displayedBitmap = markTouchPosition(originalBitmap!!, x, y)
                    imageView.setImageBitmap(displayedBitmap)
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

    // Adjust touch coordinates to bitmap coordinates
    private fun getBitmapCoordinates(touchX: Float, touchY: Float): Pair<Int, Int>? {
        if (imageView.drawable == null || originalBitmap == null) return null

        val scaleX = imageView.width.toFloat() / originalBitmap!!.width
        val scaleY = imageView.height.toFloat() / originalBitmap!!.height
        val scaleFactor = scaleX.coerceAtMost(scaleY)

        val offsetX = (imageView.width - originalBitmap!!.width * scaleFactor) / 2
        val offsetY = (imageView.height - originalBitmap!!.height * scaleFactor) / 2

        val adjustedX = ((touchX - offsetX) / scaleFactor).toInt()
        val adjustedY = ((touchY - offsetY) / scaleFactor).toInt()

        return if (adjustedX in 0 until originalBitmap!!.width && adjustedY in 0 until originalBitmap!!.height) {
            Pair(adjustedX, adjustedY)
        } else {
            null
        }
    }

    private fun markTouchPosition(bitmap: Bitmap, x: Int, y: Int): Bitmap {
        val markedBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(markedBitmap)
        val paint = Paint().apply {
            color = Color.RED
            style = Paint.Style.FILL
            strokeWidth = 10f
        }
        canvas.drawCircle(x.toFloat(), y.toFloat(), 20f, paint)
        return markedBitmap
    }
}
