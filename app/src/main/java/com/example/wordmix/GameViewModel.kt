package com.example.wordmix

import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.android.unscramble.ui.Cell
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

    private val allTiles: ArrayList<ArrayList<Tile>>
        get() = _uiState.value.allTiles ?: arrayListOf()

    private val directions = arrayOf(Direction(-1,0), Direction(1,0),
        Direction(0,-1), Direction(0,1))
    private var stack = ArrayDeque<Tile>()
    private var currentWord: String = ""
    var words: ArrayList<String> = ArrayList()
    private var stackTile = ArrayDeque<Tile>()
    private val guessedWords: ArrayList<ArrayList<Tile>> = ArrayList<ArrayList<Tile>>()

    private var counter = 0

    init {
        restartGame()
    }

    fun restartGame() {
        if(allTiles != null){
            counter = 0
            stackTile.clear()
            words.clear()
            createTiles()
            updateState()
        }
    }

    fun resetGame(){
        counter = 0
        stackTile.clear()
        for(row in allTiles){
            for(tile in row){
                val newTile = tile.copy(blocked = false)
                newTile.let{
                    it.allowed = true
                    it.blocked = false
                    it.pressed = false
                }
                updateTile(newTile)
            }
        }
    }

    fun unBlockWord(tile: Tile){
        val wordLocation = findGuessedWord(tile)
        Log.d("EEE", "${guessedWords.size}")
        Log.d("EEE", "$wordLocation")
        if (wordLocation != null){

            for(t in wordLocation){
                val newTile = t.copy(blocked = false, pressed = false)
                updateTile(newTile)
            }

            guessedWords.remove(wordLocation)
        }
        editMode(false)

    }

    fun findGuessedWord(tile: Tile): ArrayList<Tile>? {
        for(row in guessedWords){
            for(t in row){
                if(getTile(tile.row, tile.column) == getTile(t.row, t.column)){
                    return row
                }
            }
        }
        return null
    }

    fun editMode(value: Boolean){
        _uiState.value = _uiState.value.copy(editMode = value)
    }


    private fun createTiles() {
        val uiState = _uiState.value
        val emptyTiles = ArrayList<ArrayList<Tile>>()
        for (i in 0 until uiState.rows) {
            val row = ArrayList<Tile>()
            for (j in 0 until uiState.columns) {
                row.add(Tile(i, j, uiState.size, uiState.defaultText))
            }
            emptyTiles.add(row)
        }
        _uiState.value = GameUiState(allTiles = emptyTiles)
        fillTiles()
    }

    private fun fillTiles(){
        for(i in 0 until _uiState.value.rows){
            for(j in 0 until _uiState.value.columns){
                if(freeTile(i,j)){
                    if(inputWord(i,j)){
                        words.add(currentWord)
                        for (tile in stack)
                        {
                            getTile(tile.row, tile.column).text = tile.text
                        }
                    }
                }
                getTile(i, j).let {
                    it.allowed = true
                    it.blocked = false
                    it.pressed = false
                }
            }
        }
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

    private fun inputWord(row: Int, column: Int):Boolean{
        currentWord = pickRandomWord()
        stack.clear()
        val uiState = _uiState.value
        var tile = Tile(row, column, uiState.size, currentWord[0].toString())
        stack.push(tile)
        inputLetter(1)

        return !stack.isEmpty()

    }

    private fun inputLetter(index: Int): Boolean{
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
                    if(inputLetter(index+1))
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

    private fun freeTile(row: Int, column: Int): Boolean {
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

    private fun directionExist(direction: Direction): Boolean{
        val uiState = _uiState.value
        if(direction.row < 0 || direction.column < 0 || direction.row >= uiState.rows || direction.column >= uiState.columns)
            return false

        return true
    }

    fun stackWord():String{
        var wordInStack = ""
        for(tile in stackTile){
            wordInStack+=tile.text
        }
        wordInStack = wordInStack.reversed()
        return wordInStack
    }

    fun guessWord(){

        if(words.contains(stackWord())){
            val row = ArrayList<Tile>()
            for(tile in stackTile){
                row.add(tile)
                val newTile = tile.copy(blocked = true)
                updateTile(newTile)
            }
            guessedWords.add(row)
            stackTile.clear()
            setAllowAllTiles(true)
        }

    }

    fun pressTile(tile: Tile) {
        if (!tile.blocked) {
            if(tile.allowed){
                val newTile = tile.copy(pressed = !tile.pressed)
                updateTile(newTile)

                if(tileInStack(tile))
                    stackTile.pop()
                else
                    stackTile.push(newTile)

                if (!stackTile.isEmpty()){
                    setAllowAllTiles(false)
                    setAllowed(getFromStack(stackTile.peek()))
                }
                else
                    setAllowAllTiles(true)

                guessWord()
            }
        }
    }

    fun tileInStack(tile: Tile):Boolean{
        for(t in stackTile){
            if(t.row == tile.row && t.column == tile.column)
                return true
        }

        return false
    }

    fun getFromStack(tile: Tile): Tile{
        return getTile(tile.row, tile.column)
    }

    fun setAllowed(pressedTile: Tile){
        val newPress = pressedTile.copy(allowed = true)
        updateTile(newPress)
        for(dir in directions){
            val newDir = addDirection(pressedTile,dir)
            if(directionExist(newDir)){
                var allowedTile = getTile(newDir.row,newDir.column)
                if(!tileInStack(allowedTile))
                {
                    allowedTile = getTile(newDir.row,newDir.column).copy(allowed = true)
                    updateTile(allowedTile)
                }
            }
        }
    }

    fun addDirection(tile: Tile, direction: Direction):Direction{
        return Direction(direction.row + tile.row, direction.column + tile.column)
    }

    fun setAllowAllTiles(allowed: Boolean){
        for(row in allTiles){
            for(tile in row){
                if(!tile.blocked){
                    val newTile = tile.copy(allowed = allowed)
                    updateTile(newTile)
                }
            }
        }
    }

    fun getTile(row: Int, column: Int): Tile {
        return allTiles[row][column]
    }

    private fun updateTile(tile: Tile) {
        if (tile.pressed) {
            counter++
        } else {
            counter--
        }
        allTiles[tile.row][tile.column] = tile
        updateState()
    }

    private fun updateState() {
        _uiState.value = _uiState.value.copy(pressedCounter = counter)
    }

}