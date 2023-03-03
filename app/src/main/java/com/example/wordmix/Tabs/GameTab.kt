package com.example.wordmix.Tabs

import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.android.unscramble.ui.*
import com.example.wordmix.GameViewModel
import com.example.wordmix.R
import com.example.wordmix.ui.theme.GameUiState
import com.example.wordmix.ui.theme.Tile
import com.example.wordmix.ui.theme.border
import com.example.wordmix.ui.theme.color

@Composable
fun GameTab(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel,
    gameUiState: GameUiState
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(top = 20.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.07f),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            Text(text  = viewModel.guessNumber(),
                fontSize = 24.sp,
                textAlign = TextAlign.Center)
            Text(text  = viewModel.score(),
                fontSize = 24.sp,
                textAlign = TextAlign.Center)
            Row(modifier = Modifier
                .fillMaxWidth(0.4f)
                .fillMaxHeight(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Start){
                Text(text  = viewModel.combo(),
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center)
                Gif(viewModel)

            }

        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            for (i in 0 until gameUiState.rows) {
                Row {
                    for (j in 0 until gameUiState.columns) {
                        viewModel.getTile(i, j).let{
                            Cell(tile = it,
                                pressedNum = viewModel.pressedNum,
                                press = { viewModel.pressTile(it) },
                                longPress = { viewModel.longPressTile() })
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
            CustomButton(enable = viewModel.enableNextButton(),
                text = "Далі",
                press = { viewModel.setWinDailog(true)})
            CustomButton(enable = viewModel.allowEditButton(),
                text = "Відміна",
                press = { viewModel.editMode()})
            CustomButton(enable = true,
                text = "Кінець",
                press = { viewModel.setFinishDailog(true)})

        }


    }

    if(gameUiState.showWinDialog){
        WinDialog(text = viewModel.winDialogText(),
            setState = {viewModel.setWinDailog(false)},
            nextRound = { viewModel.nextRound() })
    }

    if(gameUiState.showFinishDialog){
        FinishDialog(text = viewModel.finishDialogText(),
            setState = {viewModel.setFinishDailog(false)},
            finish = { viewModel.finishGame()})
    }

    if(gameUiState.editMode){
        EditDialog(action = {viewModel.unBlockWord()},
            setState = { viewModel.editMode()})
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
                )
                .wrapContentSize()
        )
    }


}

@Composable
fun Gif(
    viewModel: GameViewModel
) {
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    val fireSize = viewModel.fireSize()

    Image(
        painter = rememberAsyncImagePainter(R.drawable.fire, imageLoader),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize(fireSize)
            .padding(bottom = 5.dp)
    )
}

@Composable
fun WinDialog(
    text: String,
    setState: () -> Unit,
    nextRound: ()->Unit
) {
    MaterialTheme {
        AlertDialog(
            onDismissRequest = setState,
            shape = MaterialTheme.shapes.small,
            title = {
                Text("Почати наступний раунд?")
            },
            text = {
                Text(text = text)
            },
            confirmButton = {
                Button(
                    onClick = nextRound,
                    shape = RoundedCornerShape(100)) {
                    Text("Наступний раунд")
                }
            },
            dismissButton = {
                Button(
                    onClick = setState,
                    shape = RoundedCornerShape(100)) {
                    Text("Назад")
                }
            }
        )

    }
}

@Composable
fun FinishDialog(
    text: String,
    setState: () -> Unit,
    finish: ()->Unit
) {
    MaterialTheme {
        AlertDialog(
            onDismissRequest = setState,
            shape = MaterialTheme.shapes.small,
            title = {
                Text("Завершити гру?")
            },
            text = {
                Text(text = text)
            },
            confirmButton = {
                Button(
                    onClick = finish,
                    shape = RoundedCornerShape(100)) {
                    Text("Завершити")
                }
            },
            dismissButton = {
                Button(
                    onClick = setState,
                    shape = RoundedCornerShape(100)) {
                    Text("Назад")
                }
            }
        )

    }
}

@Composable
fun EditDialog(
    setState: () -> Unit,
    action: ()->Unit
) {
    MaterialTheme {
        AlertDialog(
            onDismissRequest = setState,
            shape = MaterialTheme.shapes.small,
            title = {
                Text(text = "Відмінити попереднє слово?")
            },
            text = {
                Text("Вартість: -150")
            },
            confirmButton = {
                Button(
                    onClick = action,
                    shape = RoundedCornerShape(100)) {
                    Text("Відміна")
                }
            },
            dismissButton = {
                Button(
                    onClick = setState,
                    shape = RoundedCornerShape(100)) {
                    Text("Назад")
                }
            }
        )

    }
}