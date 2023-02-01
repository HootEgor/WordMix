package com.example.wordmix.ui.theme

import androidx.compose.ui.graphics.Color

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


