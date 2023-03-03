package com.example.wordmix

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wordmix.GO.User
import com.example.wordmix.GO.UserNetwork
import com.example.wordmix.data.allWords
import com.example.wordmix.ui.theme.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.pow
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import okhttp3.internal.wait


class GameViewModel(application: Application): AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    var _uiState = mutableStateOf(GameUiState())
        private set

    val apiService = UserNetwork.retrofit
    var userToken: String = ""

    private var synchronize = false

    private val allTiles: ArrayList<ArrayList<Tile>>
        get() = _uiState.value.allTiles ?: arrayListOf()

    private var leaderBoard: ArrayList<ScoreCell> = ArrayList()

//    private val language: Int
//        get () = _uiState.value.language

    private val directions = arrayOf(Direction(-1,0), Direction(1,0),
        Direction(0,-1), Direction(0,1))
    private var stack = ArrayDeque<Tile>()
    private var currentWord: String = ""
    var words: ArrayList<String> = ArrayList()
    private var stackTile = ArrayDeque<Tile>()
    private val guessedWords: ArrayList<ArrayList<Tile>> = ArrayList()

    private var counter = 0
    private var isLongPressed = false
    var pressedNum = 0

    private val gradientFrom = 1.0f
    private val gradientTo = 0.7f

    private var score = 0
    private var comboValue = 0
    private val point = 100
    private var bonus = 0


    init {
        authUser()

    }

    fun setLeaderBoard(language: Int){
        _uiState.value = _uiState.value.copy(leaderBoardLanguage = language)
    }

    private fun createLeaderBoard(){
        viewModelScope.launch {
            leaderBoard = apiService.getLeaders()
            setTab(4)
        }
    }

    fun switchToLeaderBoard() {
        createLeaderBoard()
    }

    suspend fun login(login: String, password: String){
        viewModelScope.launch {
            val user = User(Login = login, Password = password)
            val respost = apiService.loginUser(user)
            if(respost.code() == 200){
                userToken = respost.body().toString()
                saveUserTokenToSP(userToken)
            }
        }.join()
    }

    fun loginUser(login: String, password: String){
        viewModelScope.launch {
            login(login,password)
            setTab(2)
        }
    }

    fun logoutUser(){
        userToken = ""
        saveUserTokenToSP(userToken)
        setTab(2)
    }

    suspend fun signup(login: String, password: String){
        viewModelScope.launch {
            val user = User(Login = login, Password = password)
            val respost = apiService.register(user)
            if(respost.code() == 200){
                userToken = respost.body().toString()
                saveUserTokenToSP(userToken)
            }
        }.join()
    }

    fun signupUser(login: String, password: String){
        viewModelScope.launch {
            signup(login,password)
            setTab(2)
        }
    }

    private fun authUser(){
        viewModelScope.launch {
            getUserTokenFromSP()
            try{
                val userID = getUserIDFromToken()
                val user = apiService.getUserInfo(userID)
                login(user.Login, user.Password)
            }catch (e: Exception){
                userToken = ""
                Log.d("EEE", "$e" )
            }
        }
    }

    @JvmName("getLeaderBoard1")
    fun getLeaderBoard(): List<ScoreCell>? {
        return leaderBoard.filter { s -> s.Language == _uiState.value.leaderBoardLanguage }
    }

    fun getUserHistory(): List<ScoreCell>? {
//        var userHistory: List<ScoreCell>
//        viewModelScope.launch {
//            viewModelScope.launch {
//                userHistory = apiService.getUserHistory(getUserIDFromToken())
//            }.join()
//        }

        val userID = getUserIDFromToken()
        return leaderBoard.filter { s -> s.UserID == userID}
    }

    fun setTab(index: Int){
        if (index == 1)
            restartGame()

        if (userToken != "" && index == 2){
            setTab(3)
        }else{
            _uiState.value = _uiState.value.copy(tab = index)
        }

    }

    fun restartGame() {

        score = 0
        comboValue = 0
        bonus = 0

        nextRound()
    }

    fun nextRound(){
        viewModelScope.launch {
            setWinDailog(false)
            score+=bonus
            pressedNum = 0
            counter = 0
            stackTile.clear()
            guessedWords.clear()
            words.clear()
            createTiles()
            updateState()
        }

    }

    fun guessNumber(): String{
        return "${guessedWordsNumber()}/${words.size-1}"
    }

    fun score(): String{
        return "${score}"
    }

    fun combo(): String{
        return "+ ${(point+comboValue)}"
    }

    fun setWinDailog(set: Boolean){
        _uiState.value = _uiState.value.copy(showWinDialog = set)
    }

    fun enableNextButton():Boolean{
        if (guessedWordsNumber() >= words.size/3)
            return true

        return false
    }

    fun winDialogText(): String{
        var text = "Бонусних балів: +"
        bonus = guessedWordsNumber().toFloat().pow(2).toInt()
        bonus = bonus*point/10
        text+= bonus.toString()
        return text
    }

    fun setLanguage(index: Int){
        _uiState.value = _uiState.value.copy(language = index)
    }

    fun editMode(){
        longPressTile()
        _uiState.value = _uiState.value.copy(editMode = !_uiState.value.editMode)
    }

    fun allowEditButton(): Boolean{
        return score>=150
    }

    fun fireSize(): Float {
        if(comboValue>100)
            return 1.0f

        return (comboValue.toFloat()/100)
    }

    fun currentLanguageText():String{
        var text = ""
        viewModelScope.launch {
            text =  when(_uiState.value.language){
                0 -> "Українська"
                1 -> "English"
                2 -> "Español"
                else -> "Error"
            }
        }

        return text
    }

    fun languageText(index: Int): String{
        var text = ""
        viewModelScope.launch {
            text =  when(index){
                0 -> "Українська"
                1 -> "English"
                2 -> "Español"
                else -> "Error"
            }
        }

        return text
    }

    fun unBlockWord(){
        val wordLocation = findGuessedWord(guessedWords.last()[0])
        if (wordLocation != null && score >= 150){

            for(t in wordLocation){
                val newTile = t.copy(blocked = false, pressed = false)
                updateTile(newTile)
            }

            guessedWords.remove(wordLocation)
            score-=150
            isLongPressed = true
        }
        editMode()
    }



    fun guessWord(){
        val word = stackWord()
        if(allWords[_uiState.value.language].contains(word)){
            val row = ArrayList<Tile>()
            val color = randomColor()
            val alphaGradient = linspace(gradientFrom,gradientTo,stackTile.size).reversed()
            var i = 0
            for(tile in stackTile){

                row.add(tile)
                val newTile = tile.copy(blocked = true)
                newTile.let{
                    it.colorValue = color
                    it.alpha = alphaGradient[i]
                }
                updateTile(newTile)
                i++
            }

            if(word.length == pressedNum && !isLongPressed){
                score+= (point+comboValue)
                comboValue+=10
            }
            else{
                comboValue = 0
                score+= point
            }

            guessedWords.add(row)
            stackTile.clear()
            setAllowAllTiles(true)
            pressedNum = 0
            isLongPressed = false

            if(guessedWords.size >= words.size-1)
                setWinDailog(true)
        }

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

    fun guessedWordsNumber():Int{
        var num = 0
        var word = ""
        for (tiles in guessedWords){
            word = ""
            for(tile in tiles){
                word+=tile.text
            }
            word = word.reversed()
            if (words.contains(word))
                num++
        }
        return num
    }



    fun pressTile(tile: Tile) {
        if(synchronize)
            return

        synchronize = true
        viewModelScope.launch{press(tile)}
        synchronize = false

    }

    @Synchronized fun press(tile: Tile){
        if (!tile.blocked) {
            if(tile.allowed){
                pressedNum++

                val newTile = getTile(tile.row,tile.column).copy(pressed = !tile.pressed)
                updateTile(newTile)

                if(tileInStack(tile)){
                    stackTile.pop()
                }
                else{
                    stackTile.push(newTile)
                }


                if (!stackTile.isEmpty()){
                    setAllowAllTiles(false)
                    setAllowed(getFromStack(stackTile.peek()))
                }
                else{
                    pressedNum = 0
                    isLongPressed = true
                    setAllowAllTiles(true)
                }


                guessWord()
            }


        }
    }

    fun longPressTile(){
        counter = 0
        if(pressedNum != 0)
            isLongPressed = true
        pressedNum = 0
        stackTile.clear()
        for(row in allTiles){
            for(tile in row){
                val newTile = tile.copy()
                newTile.let{
                    it.allowed = true
                    it.pressed = false
                }
                updateTile(newTile)
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

    fun addDirection(tile: Tile, direction: Direction):Direction{
        return Direction(direction.row + tile.row, direction.column + tile.column)
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

    fun setAllowAllTiles(allowed: Boolean){
        for(row in allTiles){
            for(tile in row){
                if(!tile.blocked && tile.allowed != allowed){
                    val newTile = tile.copy(allowed = allowed)
                    updateTile(newTile)
                }
            }
        }
    }

    fun randomColor(): Color{
        val from = 40
        val to = 230
        val RandomColor = Color(
            (from..to).random(), (from..to).random(), (from..to).random())
        return RandomColor
    }

    fun linspace(start: Float, stop: Float, num: Int): ArrayList<Float> {
        val step = (stop - start) / (num - 1)
        var arr = ArrayList<Float>()
        for(i in 0 until num){
            arr.add(start+i*step)
        }

        return arr
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
        _uiState.value = _uiState.value.copy(allTiles = emptyTiles)//GameUiState(allTiles = emptyTiles)
        viewModelScope.launch {
            createListOfWords(uiState.rows,uiState.columns)
            fillTiles()
        }

    }

    private fun fillTiles(){
        val defaulText = _uiState.value.defaultText
        while (!filler()){
            for(i in 0 until _uiState.value.rows){
                for(j in 0 until _uiState.value.columns){
                    val newTile = getTile(i, j).copy(text = defaulText)
                    updateTile(newTile)
                }
            }
        }

        for(i in 0 until _uiState.value.rows){
            for(j in 0 until _uiState.value.columns){
                if(getTile(i, j).text == defaulText) {
                    val newTile = getTile(i, j).copy(colorValue = Color.White, blocked = true)
                    updateTile(newTile)
                }
            }
        }
    }

    fun filler():Boolean{
        var inputIndex = 0
        while(inputIndex != words.size-1){
            inputIndex = 0
            for(i in (0 until _uiState.value.rows).shuffled()){
                for(j in (0 until _uiState.value.columns).shuffled()){
                    if(freeTile(i,j)){
                        if(inputWord(i,j,inputIndex)){

                            for (tile in stack)
                            {
                                getTile(tile.row, tile.column).let{
                                    it.text = tile.text
//                                    it.colorValue = color
//                                    it.alpha = alphaGradient[i]
                                }

                            }

                            inputIndex++
                        }
                        else{
                            return false
                        }

                        if (inputIndex == words.size-1){
                            return true
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


        return true
    }

    fun createListOfWords(row: Int, column: Int){
        while (countLetters(words) != row*column){
            if(countLetters(words) > row*column){
                words.clear()
            }

            words.add(pickRandomWord())
        }

    }

    fun countLetters(arr: ArrayList<String>): Int{
        var count = 0
        for(word in arr){
            count += word.length
        }

        return count
    }

    private fun pickRandomWord(): String {
        currentWord = allWords[_uiState.value.language].random()
        if (words.contains(currentWord)) {
            return pickRandomWord()
        } else {
            return currentWord
        }
    }

    private fun inputWord(row: Int, column: Int, index: Int):Boolean{
        val uiState = _uiState.value
        for(i in 0 until 10){
            currentWord = words[index]
            stack.clear()
            val tile = Tile(row, column, uiState.size, currentWord[0].toString())
            stack.push(tile)
            inputLetter(1)

            if (!stack.isEmpty()){
                //words.add(currentWord)
                return true
            }


        }

        return false

    }

    private fun inputLetter(index: Int): Boolean{
        val uiState = _uiState.value

        var tile: Tile? = null
        val letter = currentWord[index].toString()
        val first = (0..3).random()
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

        if (getTile(row,column).text == uiState.defaultText){
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

    private fun saveUserTokenToSP(token: String){
        with(sharedPreferences.edit()) {
            putString("UserToken", token)
            apply()
        }
    }

    fun decodeJwt(token: String): DecodedJWT? {
        return try {
            JWT.decode(token)
        } catch (e: Exception) {
            null
        }
    }

    private fun getUserTokenFromSP(){
        userToken = sharedPreferences.getString("UserToken", null).toString()
    }

    private fun getUserIDFromToken(): String{
        return userToken?.let { decodeJwt(it)?.getClaim("UserID")?.asString().toString() }.toString()
    }

}