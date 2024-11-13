package com.example.sdproject

import android.graphics.Color
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.math.pow

object ColorDatabase {

    // A map of hex color codes to their names
    val colorMap = mapOf(
        // Basic Colors
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
        "#32CD32" to "Lime",

        // Light versions of colors
        "#FFCCCC" to "Light Red",
        "#CCFFCC" to "Light Green",
        "#CCCCFF" to "Light Blue",
        "#FFFFCC" to "Light Yellow",
        "#FFDAB9" to "Light Orange",
        "#E6A9EC" to "Light Purple",
        "#FFB6C1" to "Light Pink",
        "#D2B48C" to "Light Brown",
        "#D3D3D3" to "Light Gray",
        "#F0E68C" to "Light Gold",
        "#90EE90" to "Light Lime",

        // Dark versions of colors
        "#8B0000" to "Dark Red",
        "#006400" to "Dark Green",
        "#00008B" to "Dark Blue",
        "#B8860B" to "Dark Yellow",
        "#FF8C00" to "Dark Orange",
        "#4B0082" to "Dark Purple",
        "#FF1493" to "Dark Pink",
        "#8B4513" to "Dark Brown",
        "#A9A9A9" to "Dark Gray",
        "#FFD700" to "Dark Gold",
        "#006400" to "Dark Lime",
    )

    // Function to convert RGB to CIE L*a*b*
    private fun rgbToLab(rgb: Int): Triple<Float, Float, Float> {
        val r = (rgb shr 16) and 0xFF
        val g = (rgb shr 8) and 0xFF
        val bRgbComponent = rgb and 0xFF  // Renamed variable to avoid conflict

        val rNorm = r / 255.0f
        val gNorm = g / 255.0f
        val bNorm = bRgbComponent / 255.0f  // Used renamed variable

        // Apply the RGB to XYZ transformation (using the standard D65 illuminant)
        val x = rNorm * 0.4124564f + gNorm * 0.3575761f + bNorm * 0.1804375f
        val y = rNorm * 0.2126729f + gNorm * 0.7151522f + bNorm * 0.0721750f
        val z = rNorm * 0.0193339f + gNorm * 0.1191920f + bNorm * 0.9503041f

        // Normalize XYZ
        val xNorm = x / 0.95047f
        val yNorm = y / 1.00000f
        val zNorm = z / 1.08883f

        // Convert XYZ to CIE L*a*b*
        val l = if (yNorm > 0.008856f) {
            116.0f * (yNorm.toDouble().pow(1.0 / 3.0)).toFloat() - 16.0f
        } else {
            903.3f * yNorm
        }
        val a = 500.0f * ((xNorm.toDouble().pow(1.0 / 3.0)) - (yNorm.toDouble().pow(1.0 / 3.0))).toFloat()
        val b = 200.0f * ((yNorm.toDouble().pow(1.0 / 3.0)) - (zNorm.toDouble().pow(1.0 / 3.0))).toFloat()

        return Triple(l, a, b)
    }

    // Function to calculate the Euclidean distance in CIE L*a*b* space
    private fun colorDistanceLab(rgb1: Int, rgb2: Int): Float {
        val (l1, a1, b1) = rgbToLab(rgb1)
        val (l2, a2, b2) = rgbToLab(rgb2)

        // Euclidean distance in CIE L*a*b* space
        return sqrt((l1 - l2).toDouble().pow(2) + (a1 - a2).toDouble().pow(2) + (b1 - b2).toDouble().pow(2)).toFloat()
    }

    // Function to get the closest color name based on hex code
    fun getClosestColor(hexColor: String): String {
        // Convert the hex color to an RGB value
        val rgbColor = Color.parseColor(hexColor)

        // Initialize the closest match variables
        var closestColorName = "Unknown color"
        var minDistance = Float.MAX_VALUE

        // Iterate through all colors in the map to find the closest match
        for ((colorHex, colorName) in colorMap) {
            val colorRgb = Color.parseColor(colorHex)
            val distance = colorDistanceLab(rgbColor, colorRgb)

            // If the current color is closer, update the closest match
            if (distance < minDistance) {
                minDistance = distance
                closestColorName = colorName
            }
        }

        return closestColorName
    }
}
