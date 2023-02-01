package com.example.wordmix.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class Tile(
    var row: Int,
    var column: Int,
    var size: Int,
    var text: String,
    var allowed: Boolean,
    var blocked: Boolean,
    var pressed: Boolean
){
    constructor(row: Int, column: Int, size: Int, text: String)
            : this(row = row, column = column, size = size, text = text, true, false, false)
}

fun Tile.color(): Color {
    return if(blocked) Color.Green
    else if (pressed) Color.Blue else Color.Yellow
}

fun Tile.border():BorderStroke{
    return if(blocked) BorderStroke(2.dp, Color.Green)
    else if(allowed) BorderStroke(2.dp, Color.Black)
    else BorderStroke(2.dp, color())
}


