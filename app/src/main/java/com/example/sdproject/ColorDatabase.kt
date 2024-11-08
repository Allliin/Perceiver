package com.example.sdproject

import android.graphics.Color
import kotlin.math.abs

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

      /*  // Additional colors
        "#00FFFF" to "Aqua",
        "#FF00FF" to "Magenta",
        "#800000" to "Maroon",
        "#808000" to "Olive",
        "#008080" to "Teal",
        "#D3D3D3" to "Silver",
        "#8A2BE2" to "Blue Violet",
        "#4B0082" to "Indigo",
        "#7FFF00" to "Chartreuse",
        "#FF6347" to "Tomato",
        "#FF4500" to "Orange Red",
        "#2E8B57" to "Sea Green",
        "#20B2AA" to "Light Sea Green",
        "#A52A2A" to "Brown",
        "#800080" to "Purple",
        "#F5DEB3" to "Wheat",
        "#D2691E" to "Chocolate",
        "#FF8C00" to "Dark Orange"*/
    )

    // Function to calculate the Euclidean distance between two RGB colors
    fun colorDistance(rgb1: Int, rgb2: Int): Int {
        val r1 = (rgb1 shr 16) and 0xFF
        val g1 = (rgb1 shr 8) and 0xFF
        val b1 = rgb1 and 0xFF

        val r2 = (rgb2 shr 16) and 0xFF
        val g2 = (rgb2 shr 8) and 0xFF
        val b2 = rgb2 and 0xFF

        // Calculate the Euclidean distance between two RGB colors
        return abs(r1 - r2) + abs(g1 - g2) + abs(b1 - b2)
    }

    // Function to get the closest color name based on hex code
    fun getClosestColor(hexColor: String): String {
        // Convert the hex color to an RGB value
        val rgbColor = Color.parseColor(hexColor)

        // Initialize the closest match variables
        var closestColorName = "Unknown color"
        var minDistance = Int.MAX_VALUE

        // Iterate through all colors in the map to find the closest match
        for ((colorHex, colorName) in colorMap) {
            val colorRgb = Color.parseColor(colorHex)
            val distance = colorDistance(rgbColor, colorRgb)

            // If the current color is closer, update the closest match
            if (distance < minDistance) {
                minDistance = distance
                closestColorName = colorName
            }
        }

        return closestColorName
    }
}
