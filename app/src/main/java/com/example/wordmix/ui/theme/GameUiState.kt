package com.example.wordmix.ui.theme

import com.example.wordmix.GO.User

data class GameUiState(
    var rows: Int = 11,
    var columns: Int = 7,
    var size: Int = 50,
    var defaultText: String = "",
    var allTiles: ArrayList<ArrayList<Tile>>? = null,
    val pressedCounter: Int = 0,
    var editMode: Boolean = false,
    var showWinDialog: Boolean = false,
    var showFinishDialog: Boolean = false,
    var language: Int = 0,
    var leaderBoardLanguage: Int = 0,
    var tab: Int = 0,
    var creatingLeaderBoard: Boolean = true,
    var userHistory: List<ScoreCell>? = null
    )

