package com.example.android.unscramble.ui

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wordmix.GameViewModel
import com.example.wordmix.ui.theme.Tile
import com.example.wordmix.ui.theme.border
import com.example.wordmix.ui.theme.color

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = viewModel()
) {
    val gameUiState by viewModel._uiState

    Column(modifier = Modifier
        .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Row(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.05f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            Text(text  = viewModel.stackWord(),
                fontSize = 24.sp,
                textAlign = TextAlign.Center)
        }
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
                        if(!gameUiState.editMode){
                            viewModel.getTile(i, j).let{
                                Cell(tile = it,
                                    press = { viewModel.pressTile(it) },
                                    longPress = { viewModel.longPressTile() })
                            }
                        } else{
                            viewModel.getTile(i, j).let{
                                Cell(tile = it,
                                    press = { viewModel.unBlockWord(it) },
                                    longPress = { viewModel.longPressTile() })
                            }
                        }

                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            CustomButton(text = "Restart",
                        press = { viewModel.restartGame()})
            CustomButton(text = "Reset",
                press = { viewModel.resetGame()})
            CustomButton(text = "Edit",
                        press = { viewModel.editMode()})

        }


    }

}

@Composable
fun Cell(
    tile: Tile,
    press: () -> Unit,
    longPress:() -> Unit
){
    val color = tile.color()
    val border = tile.border()

    Button(
        onClick = press,
        colors= ButtonDefaults.buttonColors(backgroundColor = color),
        border = border,
        modifier = Modifier.size(tile.size.dp),
        elevation = null
    ){
        Text(text = tile.text.uppercase())
    }


}

@Composable
fun CustomButton(
    text: String,
    press: () -> Unit
){
    Button(
        onClick = press,
        colors= ButtonDefaults.buttonColors(backgroundColor = Color.Green),
        modifier = Modifier.size(100.dp)
    ){
        Text(text = text)
    }
}



@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    GameScreen()
}