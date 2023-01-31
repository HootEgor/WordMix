package com.example.android.unscramble.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wordmix.GameViewModel
import com.example.wordmix.ui.theme.Tile

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    gameViewModel: GameViewModel = viewModel()
) {
    val gameUiState by gameViewModel._uiState
    //val list by remember { mutableStateOf(gameViewModel.allTiles) }
    Log.d("EEE", "game")

    Column(modifier = Modifier
        .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Column(
            modifier = Modifier
                .fillMaxWidth().fillMaxHeight(0.8f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            for (i in 0 until gameUiState.rows) {
                Row{
                    for (j in 0 until gameUiState.columns) {
                        var tile = gameUiState.allTiles?.get(i)?.get(j)
                        //var tile = list?.get(i)?.get(j)
                        if (tile != null) {
                            if(!tile.blocked){
                                Cell(tile = tile,
                                    press = {gameViewModel.block(tile.row, tile.column)})
                            }
                            else
                                CellBlocked(tile)
                        }

                    }
                }
            }
        }

        Button(
            onClick = { if(gameUiState.allTiles != null) gameViewModel.resetGame()},
            colors= ButtonDefaults.buttonColors(backgroundColor = Color.Green),
            modifier = Modifier.size(100.dp)
        ){
            Text(gameViewModel.words.size.toString())
        }

    }

}

@Composable
fun Cell(
    tile: Tile,
    press: () -> Unit
){
    var selected by remember { mutableStateOf(false) }
    val color = if(tile.blocked) Color.Green
                else if (selected) Color.Blue else Color.Yellow
    var p = tile.blocked


    Button(
        onClick = {
            Log.d("EEE", "$p")
            selected = !selected
            if(tile.allowed)
                press()
            },
        colors= ButtonDefaults.buttonColors(backgroundColor = color),
        modifier = Modifier.size(tile.size.dp)
    ){
        Text(tile.text)
    }
}

@Composable
fun CellBlocked(tile: Tile){
    Button(
        onClick = {},
        colors= ButtonDefaults.buttonColors(backgroundColor = Color.Black),
        modifier = Modifier.size(tile.size.dp)
    ){
        Text(tile.text)
    }
}



@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    GameScreen()
}