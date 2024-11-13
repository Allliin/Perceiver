package com.example.sdproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.animation.ObjectAnimator
import android.animation.AnimatorSet
import android.graphics.Color
import android.os.Build
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var colorDetectionCard: CardView
    private lateinit var liveDetectionButton: Button
    private lateinit var galleryDetectionButton: Button
    private lateinit var subOptionLayout: View
    private lateinit var rootLayout: View  // Add root layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainmenu)

        // Initialize Views
        colorDetectionCard = findViewById(R.id.colorDetectionCard)
        subOptionLayout = findViewById(R.id.subOptionLayout)
        liveDetectionButton = findViewById(R.id.liveDetectionButton)
        galleryDetectionButton = findViewById(R.id.galleryDetectionButton)
        rootLayout = findViewById(R.id.rootLayout) // Initialize root layout

        // Set initial visibility of buttons to invisible and scaled down
        liveDetectionButton.alpha = 0f
        galleryDetectionButton.alpha = 0f
        liveDetectionButton.scaleX = 0.8f
        liveDetectionButton.scaleY = 0.8f
        galleryDetectionButton.scaleX = 0.8f
        galleryDetectionButton.scaleY = 0.8f

        // Color Detection Card OnClick
        colorDetectionCard.setOnClickListener {
            animateCardAndRevealOptions()
        }

        // Set the status bar and navigation bar color to red
        setStatusAndNavigationBarColor()

        // Set up Intent Actions for buttons
        liveDetectionButton.setOnClickListener {
            startActivity(Intent(this, LiveDetectionActivity::class.java))
        }

        galleryDetectionButton.setOnClickListener {
            startActivity(Intent(this, GalleryDetectionActivity::class.java))
        }

        // Set up touch listener for root layout to detect click outside of the card and buttons
        rootLayout.setOnClickListener {
            // If the touch is outside of the card and buttons, revert the UI
            if (subOptionLayout.visibility == View.VISIBLE) {
                revertUI()
            }
        }
    }
    private fun setStatusAndNavigationBarColor() {
        val sysuitopColor = ContextCompat.getColor(this, R.color.bgd_end)
        val sysuibottomColor = ContextCompat.getColor(this, R.color.bgd_str)
        // Set the status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 and above
            window.statusBarColor = sysuitopColor
            window.navigationBarColor = sysuibottomColor
        } else {
            // For Android versions below 11
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_FULLSCREEN
            window.statusBarColor = sysuitopColor
            window.navigationBarColor = sysuibottomColor
        }
    }

    // Animation to replace Color Detection Card with options
    private fun animateCardAndRevealOptions() {
        // Fade out and scale down the colorDetectionCard
        val fadeOut = ObjectAnimator.ofFloat(colorDetectionCard, "alpha", 1f, 0f)
        val scaleX = ObjectAnimator.ofFloat(colorDetectionCard, "scaleX", 1f, 0.8f)
        val scaleY = ObjectAnimator.ofFloat(colorDetectionCard, "scaleY", 1f, 0.8f)
        val hideAnimatorSet = AnimatorSet()
        hideAnimatorSet.playTogether(fadeOut, scaleX, scaleY)
        hideAnimatorSet.duration = 300

        // Animate sub-options to appear in place of colorDetectionCard
        hideAnimatorSet.addListener(onEnd = {
            // Make the CardView invisible and show the buttons
            colorDetectionCard.visibility = View.GONE
            subOptionLayout.visibility = View.VISIBLE  // Make subOptionLayout visible

            // Fade in and scale up buttons (Live Detection and Gallery Detection)
            val fadeInLive = ObjectAnimator.ofFloat(liveDetectionButton, "alpha", 0f, 1f)
            val fadeInGallery = ObjectAnimator.ofFloat(galleryDetectionButton, "alpha", 0f, 1f)
            val scaleUpLiveX = ObjectAnimator.ofFloat(liveDetectionButton, "scaleX", 0.8f, 1f)
            val scaleUpLiveY = ObjectAnimator.ofFloat(liveDetectionButton, "scaleY", 0.8f, 1f)
            val scaleUpGalleryX = ObjectAnimator.ofFloat(galleryDetectionButton, "scaleX", 0.8f, 1f)
            val scaleUpGalleryY = ObjectAnimator.ofFloat(galleryDetectionButton, "scaleY", 0.8f, 1f)

            // Set animations for both buttons to play together
            val showOptionsAnimatorSet = AnimatorSet()
            showOptionsAnimatorSet.playTogether(
                fadeInLive, fadeInGallery,
                scaleUpLiveX, scaleUpLiveY,
                scaleUpGalleryX, scaleUpGalleryY
            )
            showOptionsAnimatorSet.duration = 300
            showOptionsAnimatorSet.start()  // Start animations
        })

        hideAnimatorSet.start()  // Start hiding the card and revealing options
    }

    // Revert the UI to show the card and hide the buttons
    private fun revertUI() {
        // Fade in and scale up the colorDetectionCard
        val fadeIn = ObjectAnimator.ofFloat(colorDetectionCard, "alpha", 0f, 1f)
        val scaleX = ObjectAnimator.ofFloat(colorDetectionCard, "scaleX", 0.8f, 1f)
        val scaleY = ObjectAnimator.ofFloat(colorDetectionCard, "scaleY", 0.8f, 1f)
        val showAnimatorSet = AnimatorSet()
        showAnimatorSet.playTogether(fadeIn, scaleX, scaleY)
        showAnimatorSet.duration = 300

        // Animate buttons to fade out and scale down
        val fadeOutLive = ObjectAnimator.ofFloat(liveDetectionButton, "alpha", 1f, 0f)
        val fadeOutGallery = ObjectAnimator.ofFloat(galleryDetectionButton, "alpha", 1f, 0f)
        val scaleDownLiveX = ObjectAnimator.ofFloat(liveDetectionButton, "scaleX", 1f, 0.8f)
        val scaleDownLiveY = ObjectAnimator.ofFloat(liveDetectionButton, "scaleY", 1f, 0.8f)
        val scaleDownGalleryX = ObjectAnimator.ofFloat(galleryDetectionButton, "scaleX", 1f, 0.8f)
        val scaleDownGalleryY = ObjectAnimator.ofFloat(galleryDetectionButton, "scaleY", 1f, 0.8f)

        // Set animations for both buttons to play together
        val hideOptionsAnimatorSet = AnimatorSet()
        hideOptionsAnimatorSet.playTogether(
            fadeOutLive, fadeOutGallery,
            scaleDownLiveX, scaleDownLiveY,
            scaleDownGalleryX, scaleDownGalleryY
        )
        hideOptionsAnimatorSet.duration = 300

        hideOptionsAnimatorSet.addListener(onEnd = {
            // After the animations are complete, hide the buttons and show the card again
            subOptionLayout.visibility = View.GONE
            colorDetectionCard.visibility = View.VISIBLE
        })

        // Start the hide and show animations
        hideOptionsAnimatorSet.start()
        showAnimatorSet.start()
    }

    // Handle the back button press to revert the UI to its original state
    override fun onBackPressed() {
        if (subOptionLayout.visibility == View.VISIBLE) {
            revertUI()  // If buttons are visible, revert the UI
        } else {
            super.onBackPressed()  // Default back button behavior
        }
    }
}
