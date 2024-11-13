package com.example.sdproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.animation.ObjectAnimator
import android.animation.AnimatorSet
import androidx.core.animation.addListener

class MainActivity : AppCompatActivity() {

    private lateinit var colorDetectionCard: CardView
    private lateinit var liveDetectionButton: Button
    private lateinit var galleryDetectionButton: Button
    private lateinit var subOptionLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainmenu)

        // Initialize Views
        colorDetectionCard = findViewById(R.id.colorDetectionCard)
        subOptionLayout = findViewById(R.id.subOptionLayout)
        liveDetectionButton = findViewById(R.id.liveDetectionButton)
        galleryDetectionButton = findViewById(R.id.galleryDetectionButton)

        // Color Detection Card OnClick
        colorDetectionCard.setOnClickListener {
            animateCardAndRevealOptions()
        }

        // Set up Intent Actions
        liveDetectionButton.setOnClickListener {
            startActivity(Intent(this, LiveDetectionActivity::class.java))
        }

        galleryDetectionButton.setOnClickListener {
            startActivity(Intent(this, GalleryDetectionActivity::class.java))
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
            colorDetectionCard.visibility = View.GONE
            subOptionLayout.visibility = View.VISIBLE

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
            showOptionsAnimatorSet.start()
        })

        hideAnimatorSet.start()
    }
}
