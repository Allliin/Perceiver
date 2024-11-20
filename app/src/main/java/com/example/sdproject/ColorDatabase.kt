package com.example.sdproject

import com.github.ajalt.colormath.model.LAB
import com.github.ajalt.colormath.model.RGB
import kotlin.math.*

object ColorDatabase {

    private val colorMap = mapOf(
        "#FFFFFF" to "White",
        "#000000" to "Black",
        "#808080" to "Gray",
        "#C0C0C0" to "Silver",
        "#A9A9A9" to "Dark Gray",
        "#D3D3D3" to "Light Gray",  // Added Light Gray
        "#F5F5F5" to "Whitesmoke",  // Added Whitesmoke
        "#F8F8FF" to "Ghost White", // Added Ghost White
        "#FAEBD7" to "Antique White", // Added Antique White

        "#FF0000" to "Red",
        "#8B0000" to "Dark Red",
        "#FF6347" to "Tomato",
        "#FF4500" to "Orange Red",
        "#FF1493" to "Deep Pink",  // Added Deep Pink
        "#DC143C" to "Crimson",     // Added Crimson
        "#B22222" to "Firebrick",   // Added Firebrick
        "#FF0000" to "Red",         // Ensured Red is captured explicitly

        "#00FF00" to "Green",
        "#006400" to "Dark Green",
        "#32CD32" to "Lime",
        "#ADFF2F" to "Green Yellow",
        "#90EE90" to "Light Green", // Added Light Green
        "#228B22" to "Forest Green", // Added Forest Green
        "#2E8B57" to "Sea Green",   // Added Sea Green
        "#3CB371" to "Medium Sea Green", // Added Medium Sea Green

        "#0000FF" to "Blue",
        "#00008B" to "Dark Blue",
        "#87CEEB" to "Sky Blue",
        "#4682B4" to "Steel Blue",
        "#1E90FF" to "Dodger Blue", // Added Dodger Blue
        "#5F9EA0" to "Cadet Blue",  // Added Cadet Blue
        "#7EC8E3" to "Light Sky Blue", // Added Light Sky Blue
        "#4169E1" to "Royal Blue",  // Added Royal Blue

        "#FFFF00" to "Yellow",
        "#FFD700" to "Gold",
        "#FFA500" to "Orange",
        "#FFB347" to "Light Orange",
        "#FFFFE0" to "Light Yellow", // Added Light Yellow
        "#FFEC8B" to "Light Goldenrod Yellow", // Added Light Goldenrod Yellow
        "#FFD700" to "Gold",  // Ensured Gold is explicitly captured
        "#F0E68C" to "Khaki", // Added Khaki

        "#800080" to "Purple",
        "#4B0082" to "Indigo",
        "#D8BFD8" to "Thistle",
        "#EE82EE" to "Violet",
        "#9400D3" to "Dark Violet", // Added Dark Violet
        "#8A2BE2" to "Blue Violet", // Added Blue Violet
        "#DA70D6" to "Orchid", // Added Orchid
        "#9932CC" to "Dark Orchid",  // Added Dark Orchid

        "#FFC0CB" to "Pink",
        "#FF69B4" to "Hot Pink",
        "#DB7093" to "Pale Violet Red",
        "#C71585" to "Medium Violet Red",  // Added Medium Violet Red
        "#FFB6C1" to "Light Pink",  // Added Light Pink
        "#FF1493" to "Deep Pink",   // Added Deep Pink

        "#A52A2A" to "Brown",
        "#8B4513" to "Saddle Brown",
        "#D2691E" to "Chocolate",
        "#F4A460" to "Sandy Brown",
        "#A0522D" to "Sienna", // Added Sienna
        "#D2B48C" to "Tan", // Added Tan
        "#8B0000" to "Dark Red", // Added Dark Red explicitly again

        "#00FFFF" to "Cyan",
        "#E0FFFF" to "Light Cyan",
        "#008B8B" to "Dark Cyan",
        "#40E0D0" to "Turquoise",
        "#00CED1" to "Dark Turquoise", // Added Dark Turquoise
        "#48D1CC" to "Medium Turquoise", // Added Medium Turquoise
        "#00BFFF" to "Deep Sky Blue", // Added Deep Sky Blue

        "#FF00FF" to "Magenta",
        "#8B008B" to "Dark Magenta",
        "#C71585" to "Medium Violet Red",
        "#DA70D6" to "Orchid", // Added Orchid
        "#9932CC" to "Dark Orchid",  // Added Dark Orchid
        "#8B008B" to "Dark Magenta", // Ensured Dark Magenta included

        "#F5F5DC" to "Beige",
        "#FAF0E6" to "Linen",
        "#FFF8DC" to "Cornsilk",
        "#F0E68C" to "Khaki",
        "#D3D3D3" to "Light Grey", // Added Light Grey
        "#F4A300" to "Amber",  // Added Amber

        "#556B2F" to "Dark Olive Green",
        "#6B8E23" to "Olive Drab",
        "#808000" to "Olive",
        "#9ACD32" to "Yellow Green", // Added Yellow Green
        "#BDB76B" to "Dark Khaki", // Added Dark Khaki
        "#8FBC8F" to "Dark Sea Green", // Added Dark Sea Green
        "#A9A9A9" to "Dark Gray", // Reiterated Dark Gray for more robust detection
        "#6B8E23" to "Olive Drab"  // Added Olive Drab
    )


    fun getClosestColor(hexColor: String): String {
        val targetLAB = RGB(hexColor).toLAB()

        var closestColorName = "Unknown color"
        var minDistance = Double.MAX_VALUE

        for ((colorHex, colorName) in colorMap) {
            val currentLAB = RGB(colorHex).toLAB()
            val distance = calculateCIEDE2000(targetLAB, currentLAB)

            if (distance < minDistance) {
                minDistance = distance
                closestColorName = colorName
            }
        }

        return closestColorName
    }

    // Implementation of CIEDE2000
    private fun calculateCIEDE2000(lab1: LAB, lab2: LAB): Double {
        val (L1, a1, b1) = Triple(lab1.l, lab1.a, lab1.b)
        val (L2, a2, b2) = Triple(lab2.l, lab2.a, lab2.b)

        val avgL = (L1 + L2) / 2.0
        val deltaL = L2 - L1

        val c1 = sqrt(a1 * a1 + b1 * b1)
        val c2 = sqrt(a2 * a2 + b2 * b2)
        val avgC = (c1 + c2) / 2.0

        val G = 0.5 * (1 - sqrt(avgC.pow(7) / (avgC.pow(7) + 25.0.pow(7))))
        val a1Prime = a1 * (1 + G)
        val a2Prime = a2 * (1 + G)

        val c1Prime = sqrt(a1Prime * a1Prime + b1 * b1)
        val c2Prime = sqrt(a2Prime * a2Prime + b2 * b2)

        val avgCPrime = (c1Prime + c2Prime) / 2.0
        val deltaCPrime = c2Prime - c1Prime

        val h1Prime = atan2(b1.toDouble(), a1Prime).let { if (it < 0) it + 2 * Math.PI else it }
        val h2Prime = atan2(b2.toDouble(), a2Prime).let { if (it < 0) it + 2 * Math.PI else it }

        val avgHPrime = if (abs(h1Prime - h2Prime) > Math.PI) {
            (h1Prime + h2Prime + 2 * Math.PI) / 2.0
        } else {
            (h1Prime + h2Prime) / 2.0
        }

        val deltaHPrime = if (abs(h1Prime - h2Prime) > Math.PI) {
            h2Prime - h1Prime + 2 * Math.PI
        } else {
            h2Prime - h1Prime
        }

        val T = 1 - 0.17 * cos(avgHPrime - Math.PI / 6) +
                0.24 * cos(2 * avgHPrime) +
                0.32 * cos(3 * avgHPrime + Math.PI / 30) -
                0.20 * cos(4 * avgHPrime - 7 * Math.PI / 20)

        val deltaH = 2 * sqrt(c1Prime * c2Prime) * sin(deltaHPrime / 2.0)
        val deltaE = sqrt(
            (deltaL / (1.0 + 0.015 * (avgL - 50).pow(2))).pow(2) +
                    (deltaCPrime / (1.0 + 0.045 * avgCPrime)).pow(2) +
                    (deltaH / (1.0 + 0.015 * avgCPrime * T)).pow(2)
        )

        return deltaE
    }
}
