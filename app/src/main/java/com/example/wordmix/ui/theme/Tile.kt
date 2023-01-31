package com.example.wordmix.ui.theme

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




