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
import com.example.wordmix.ui.theme.color

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = viewModel()
) {
    val gameUiState by viewModel._uiState
    //val list by remember { mutableStateOf(gameViewModel.allTiles) }
    //Log.d("EEE", "game")

    //Text(text = "Selected tiles counter: ${gameUiState.pressedCounter}", modifier = Modifier.size(18.dp))

    Column(modifier = Modifier
        .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            for (i in 0 until gameUiState.rows) {
                Row{
                    for (j in 0 until gameUiState.columns) {
                        viewModel.getTile(i, j).let{
                            Cell(tile = it,
                                press = { viewModel.pressTile(it) })
                        }
                    }
                }
            }
        }

        Button(
            onClick = { if(gameUiState.allTiles != null) viewModel.resetGame()},
            colors= ButtonDefaults.buttonColors(backgroundColor = Color.Green),
            modifier = Modifier.size(100.dp)
        ){
            Text(viewModel.words.size.toString())
        }

    }

}

@Composable
fun Cell(
    tile: Tile,
    press: () -> Unit
){
    val color = tile.color()

    Button(
        onClick = press,
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