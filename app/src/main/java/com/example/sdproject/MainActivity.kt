package com.example.sdproject

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.RelativeLayout // Ensure you import the right layout type
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var selectedOptionTextView: TextView
    private lateinit var startDetectionButton: Button
    private lateinit var circularMenuContainer: RelativeLayout // Update to match the actual type in XML

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainmenu)

        // Initialize views
        selectedOptionTextView = findViewById(R.id.selectedOptionTextView)
        startDetectionButton = findViewById(R.id.startDetectionButton)
        circularMenuContainer = findViewById(R.id.circularMenuContainer) // Ensure this matches the layout type

        val liveDetectionButton: ImageButton = findViewById(R.id.liveDetectionButton)
        val galleryDetectionButton: ImageButton = findViewById(R.id.galleryDetectionButton)

        // Set up click listeners for each radial button
        liveDetectionButton.setOnClickListener {
            selectedOptionTextView.text = "Live Detection"
            selectedOptionTextView.setTextColor(Color.WHITE)
            circularMenuContainer.setBackgroundColor(Color.parseColor("#FFCC00"))

            startDetectionButton.setOnClickListener {
                startActivity(Intent(this, LiveDetectionActivity::class.java))
            }
        }

        galleryDetectionButton.setOnClickListener {
            selectedOptionTextView.text = "Gallery Detection"
            selectedOptionTextView.setTextColor(Color.WHITE)
            circularMenuContainer.setBackgroundColor(Color.parseColor("#00CCFF"))

            startDetectionButton.setOnClickListener {
                startActivity(Intent(this, GalleryDetectionActivity::class.java))
            }
        }
    }
}
