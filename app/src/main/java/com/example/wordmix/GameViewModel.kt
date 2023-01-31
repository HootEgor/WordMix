package com.example.wordmix

import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.android.unscramble.ui.Cell
import com.example.android.unscramble.ui.CellBlocked
import com.example.android.unscramble.ui.GameScreen
import com.example.wordmix.data.allWords
import com.example.wordmix.ui.theme.Direction
import com.example.wordmix.ui.theme.GameUiState
import com.example.wordmix.ui.theme.Tile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.ArrayDeque
import kotlin.random.Random

class GameViewModel: ViewModel() {

    var _uiState = mutableStateOf(GameUiState())
        private set
    //val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val directions = arrayOf(Direction(-1,0), Direction(1,0),
                                    Direction(0,-1), Direction(0,1))
    private var stack = ArrayDeque<Tile>()
    private var currentWord: String = ""
    var words: ArrayList<String> = ArrayList()

    init {
        resetGame()
    }

    fun resetGame() {
        words.clear()
        createTiles()
    }

    fun createTiles() {
        val uiState = _uiState.value
        var allTiles = ArrayList<ArrayList<Tile>>()
        for (i in 0 until uiState.rows) {
            var row = ArrayList<Tile>()
            for (j in 0 until uiState.columns) {
                row.add(Tile(i, j, uiState.size, uiState.defaultText))
            }
            allTiles.add(row)
        }
        _uiState.value = GameUiState(allTiles = allTiles)
        fillTiles()
        //block(0,1)
    }

    fun fillTiles(){
        val copyList = _uiState.value.allTiles
        for(i in 0 until _uiState.value.rows){
            for(j in 0 until _uiState.value.columns){
                if(freeTile(i,j)){
                    if(inputWord(i,j)){
                        words.add(currentWord)
                        for (tile in stack)
                        {
                            copyList?.get(tile.row)?.get(tile.column)?.text = tile.text
                        }
                    }
                }
                copyList?.get(i)?.get(j)?.allowed = true
                copyList?.get(i)?.get(j)?.blocked = false
                copyList?.get(i)?.get(j)?.pressed = false
            }
        }
        _uiState.value = _uiState.value.copy(allTiles = copyList)
    }

    private fun pickRandomWord(): String {
        currentWord = allWords.random()
        if (words.contains(currentWord)) {
            return pickRandomWord()
        } else {
            words.add(currentWord)
            return currentWord
        }
    }

    fun inputWord(row: Int, column: Int):Boolean{
        currentWord = pickRandomWord()
        stack.clear()
        val uiState = _uiState.value
        var tile = Tile(row, column, uiState.size, currentWord[0].toString())
        stack.push(tile)
        inputLetter(row, column, 1)

        return !stack.isEmpty()

    }

    fun inputLetter(row: Int, column: Int, index: Int): Boolean{
        val uiState = _uiState.value

        var tile: Tile? = null
        var letter = currentWord[index].toString()
        var first = (0..3).random()
        var id: Int
        var newRow: Int
        var newColumn: Int
        for(i in 0..3){
            id = first+i
            if(id>3)
                id -= 4
            newRow = stack.peek().row+directions[id].row
            newColumn = stack.peek().column+directions[id].column
            if(freeTile(newRow,newColumn))
            {
                tile = Tile(newRow, newColumn, uiState.size, letter)
                stack.push(tile)
                if(stack.size < currentWord.length){
                    if(inputLetter(newRow,newColumn,index+1))
                        return true
                }
                else
                    return true

            }
        }
        if(stack.size < currentWord.length)
            stack.pop()

        return false

    }

    fun freeTile(row: Int, column: Int): Boolean {
        val uiState = _uiState.value
        if(row < 0 || column < 0 || row >= uiState.rows || column >= uiState.columns)
            return false

        if (uiState.allTiles?.get(row)?.get(column)?.text == uiState.defaultText){
            for(tile in stack){
                if(tile.row == row && tile.column == column){
                    return false
                }
            }
            return true
        }




        return false
    }

    fun block(row:Int, column: Int){
        val copyList = _uiState.value.allTiles
        copyList?.get(row)?.get(column)?.blocked = !copyList?.get(row)?.get(column)?.blocked!!
        //copyList?.get(row)?.get(column)?.text = "W"
        Log.d("EEE", "blocked $row : $column")
        _uiState.value = _uiState.value.copy(allTiles = copyList)

    }

    fun press(row:Int, column: Int){
        val copyList = _uiState.value.allTiles
        copyList?.get(row)?.get(column)?.pressed = !copyList?.get(row)?.get(column)?.pressed!!
        var p = copyList?.get(row)?.get(column)?.pressed
        Log.d("EEE", "pressed $p")
        _uiState.value = _uiState.value.copy(allTiles = copyList)
        //_uiState.value = GameUiState(allTiles = copyList)
    }


}