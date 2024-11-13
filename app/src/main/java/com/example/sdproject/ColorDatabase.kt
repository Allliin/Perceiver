package com.example.sdproject

import android.graphics.Color
import kotlin.math.pow
import kotlin.math.sqrt

object ColorDatabase {

    private val colorMap = mapOf(
        "#FFFFFF" to "White",
        "#000000" to "Black",
        "#FF0000" to "Red",
        "#00FF00" to "Green",
        "#0000FF" to "Blue",
        "#FFFF00" to "Yellow",
        "#FFA500" to "Orange",
        "#800080" to "Purple",
        "#FFC0CB" to "Pink",
        "#A52A2A" to "Brown",
        "#808080" to "Gray",
        "#FFD700" to "Gold",
        "#32CD32" to "Lime"
        // Add more colors as needed
    )

    // Define RGB ranges for color categories
    private val redRange = Triple(150..255, 0..100, 0..100)  // Red range (high red, low green, low blue)
    private val greenRange = Triple(0..100, 150..255, 0..100) // Green range (low red, high green, low blue)
    private val blueRange = Triple(0..100, 0..100, 150..255) // Blue range (low red, low green, high blue)
    private val yellowRange = Triple(150..255, 150..255, 0..100) // Yellow range (high red, high green, low blue)

    // Improved Orange Range: adjusted to reduce overlap with red
    private val orangeRange = Triple(200..255, 100..200, 0..100)  // Orange range (high red, moderate green, low blue)

    private val purpleRange = Triple(100..150, 0..50, 100..150) // Purple range (moderate red, low green, moderate blue)

    // Define Light and Dark variants for existing colors
    private val lightRedRange = Triple(255..255, 50..150, 50..150)
    private val darkRedRange = Triple(100..150, 0..50, 0..50)

    private val lightGreenRange = Triple(50..150, 255..255, 50..150)
    private val darkGreenRange = Triple(0..50, 100..150, 0..50)

    private val lightBlueRange = Triple(50..150, 50..150, 255..255)
    private val darkBlueRange = Triple(0..50, 0..50, 100..150)

    private val lightYellowRange = Triple(255..255, 255..255, 0..50)
    private val darkYellowRange = Triple(150..200, 150..200, 0..50)

    private val lightOrangeRange = Triple(255..255, 150..200, 0..50)
    private val darkOrangeRange = Triple(150..200, 50..100, 0..50)

    private val lightPurpleRange = Triple(150..200, 0..50, 150..200)
    private val darkPurpleRange = Triple(50..100, 0..50, 50..100)

    // Check if a color is within a defined RGB range
    private fun isInRange(r: Int, g: Int, b: Int, range: Triple<IntRange, IntRange, IntRange>): Boolean {
        return r in range.first && g in range.second && b in range.third
    }

    // Function to get the closest color based on RGB ranges
    fun getClosestColor(hexColor: String): String {
        val rgbColor = Color.parseColor(hexColor)
        val r = (rgbColor shr 16) and 0xFF
        val g = (rgbColor shr 8) and 0xFF
        val b = rgbColor and 0xFF

        // Check for specific color ranges
        when {
            isInRange(r, g, b, redRange) -> return "Red"
            isInRange(r, g, b, greenRange) -> return "Green"
            isInRange(r, g, b, blueRange) -> return "Blue"
            isInRange(r, g, b, yellowRange) -> return "Yellow"
            isInRange(r, g, b, orangeRange) -> return "Orange"
            isInRange(r, g, b, purpleRange) -> return "Purple"

            // Light and Dark variants
            isInRange(r, g, b, lightRedRange) -> return "Light Red"
            isInRange(r, g, b, darkRedRange) -> return "Dark Red"

            isInRange(r, g, b, lightGreenRange) -> return "Light Green"
            isInRange(r, g, b, darkGreenRange) -> return "Dark Green"

            isInRange(r, g, b, lightBlueRange) -> return "Light Blue"
            isInRange(r, g, b, darkBlueRange) -> return "Dark Blue"

            isInRange(r, g, b, lightYellowRange) -> return "Light Yellow"
            isInRange(r, g, b, darkYellowRange) -> return "Dark Yellow"

            isInRange(r, g, b, lightOrangeRange) -> return "Light Orange"
            isInRange(r, g, b, darkOrangeRange) -> return "Dark Orange"

            isInRange(r, g, b, lightPurpleRange) -> return "Light Purple"
            isInRange(r, g, b, darkPurpleRange) -> return "Dark Purple"
        }

        // Default color mapping if no range match found
        for ((colorHex, colorName) in colorMap) {
            val colorRgb = Color.parseColor(colorHex)
            val distance = colorDistance(rgbColor, colorRgb)
            if (distance < 100) { // Adjust the threshold based on precision you need
                return colorName
            }
        }

        return "Unknown color"
    }

    // Calculate the Euclidean distance between two RGB colors
    private fun colorDistance(rgb1: Int, rgb2: Int): Double {
        val r1 = (rgb1 shr 16) and 0xFF
        val g1 = (rgb1 shr 8) and 0xFF
        val b1 = rgb1 and 0xFF

        val r2 = (rgb2 shr 16) and 0xFF
        val g2 = (rgb2 shr 8) and 0xFF
        val b2 = rgb2 and 0xFF

        return sqrt(
            ((r1 - r2).toDouble().pow(2) + (g1 - g2).toDouble().pow(2) + (b1 - b2).toDouble().pow(2)).toFloat()
                .toDouble()
        )
    }
}
