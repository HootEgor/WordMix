package com.example.wordmix.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class Tile(
    var row: Int,
    var column: Int,
    var size: Int,
    var text: String,
    var allowed: Boolean,
    var blocked: Boolean,
    var pressed: Boolean,
    var colorValue: Color,
    var alpha: Float
){
    constructor(row: Int, column: Int, size: Int, text: String)
            : this(row = row, column = column, size = size, text = text,
                true, false, false, Color.Black, 0.5f)
}

fun Tile.color(): Color {
    return if(blocked) colorValue.copy(alpha = alpha)
    else if (pressed) Color.Gray else Color.LightGray
}

fun Tile.border(pressedNum: Int):BorderStroke?{
    val nullBorder = BorderStroke(2.dp, Color.White.copy(alpha = 0.0f))
    return if(pressedNum == 0) nullBorder
    else if(blocked) nullBorder
    else if(allowed) BorderStroke(2.dp, Color.DarkGray)
    else nullBorder

}

