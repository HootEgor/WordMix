package com.example.wordmix.ui.theme

import java.util.ArrayDeque

data class GameUiState(
    var rows: Int = 11,
    var columns: Int = 7,
    var size: Int = 50,
    var defaultText: String = "",
    var allTiles: ArrayList<ArrayList<Tile>>? = null,
    val pressedCounter: Int = 0,
    var editMode: Boolean = false
)
