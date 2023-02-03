package com.example.android.unscramble.ui

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
        .fillMaxSize().padding(top = 20.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Row(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.07f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            Text(text  = viewModel.guessNumber(),
                fontSize = 24.sp,
                textAlign = TextAlign.Center)
            Text(text  = viewModel.score(),
                fontSize = 24.sp,
                textAlign = TextAlign.Center)
            Text(text  = viewModel.combo(),
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
                                    pressedNum = viewModel.pressedNum,
                                    press = { viewModel.pressTile(it) },
                                    longPress = { viewModel.longPressTile() })
                            }
                        } else{
                            viewModel.getTile(i, j).let{
                                Cell(tile = it,
                                    pressedNum = viewModel.pressedNum,
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
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            CustomButton(text = "Далі",
                        press = { viewModel.restartGame()})
//            CustomButton(text = "Reset",
//                press = { viewModel.resetGame()})
            CustomButton(text = "Відміна",
                        press = { viewModel.editMode()})

        }


    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Cell(
    tile: Tile,
    pressedNum: Int,
    press: () -> Unit,
    longPress:() -> Unit
){
    val color = tile.color()
    val border = tile.border(pressedNum)

    Surface(
        color= color,
        border = border,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.size(tile.size.dp),

    ){
        Text(text = tile.text.lowercase(),
            fontSize = 20.sp,
            modifier = Modifier
                .combinedClickable(
                    onClick = press,
                    onLongClick = longPress
                ).wrapContentSize()
        )
    }


}

@Composable
fun CustomButton(
    text: String,
    press: () -> Unit
){
    Button(
        onClick = press,
        shape = RoundedCornerShape(16.dp),
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