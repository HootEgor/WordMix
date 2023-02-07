package com.example.android.unscramble.ui

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wordmix.GameViewModel
import com.example.wordmix.ui.theme.Tile
import com.example.wordmix.ui.theme.border
import com.example.wordmix.ui.theme.color
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.wordmix.R

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = viewModel()
) {
    val gameUiState by viewModel._uiState

    when(gameUiState.tab){
        0 -> {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(text  = "Оберіть мову:",
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center)

                var expanded by remember { mutableStateOf(false) }

                Box {
                    Text(text = viewModel.currentLanguageText(),
                        fontSize=18.sp,
                        modifier = Modifier
                        .padding(10.dp)
                        .clickable(onClick={ expanded = !expanded}))
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        CustomText(text = viewModel.languageText(0),
                            press = {expanded = !expanded
                                viewModel.setLanguage(0)})
                        Divider()
                        CustomText(text = viewModel.languageText(1),
                            press = {expanded = !expanded
                                viewModel.setLanguage(1)})
                        Divider()
                        CustomText(text = viewModel.languageText(2),
                            press = {expanded = !expanded
                                viewModel.setLanguage(2)})
                    }
                }

                CustomButton(enable = true,
                    text = "Почати",
                    press = { viewModel.setTab(1)})
            }
        }
        1 -> {
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
                    CustomButton(enable = viewModel.enableNextButton(),
                        text = "Далі",
                        press = { viewModel.setWinDailog(true)})
                    CustomButton(enable = true,
                        text = "Відміна",
                        press = { viewModel.editMode()})
                    CustomButton(enable = true,
                        text = "Back",
                        press = { viewModel.setTab(0)})

                }


            }

            if(gameUiState.showWinDialog){
                WinDialog(text = viewModel.winDialogText(),
                    setState = {viewModel.setWinDailog(false)},
                    nextRound = { viewModel.nextRound() })
            }
        }
        2 -> {}
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
fun CustomButton(
    enable: Boolean,
    text: String,
    press: () -> Unit
){
    Button(
        onClick = press,
        shape = RoundedCornerShape(100),
        colors= ButtonDefaults.buttonColors(MaterialTheme.colors.primaryVariant),
        modifier = Modifier
            .height(50.dp)
            .width(100.dp),
        enabled = enable
    ){
        Text(text = text)
    }

}

@Composable
fun CustomText(
    text: String,
    press: () -> Unit
){

    Text(text = text,
        fontSize=18.sp,
        modifier = Modifier
            .padding(10.dp)
            .clickable(onClick=press)
    )
}

@Composable
fun Gif(
    viewModel: GameViewModel
) {
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            if (SDK_INT >= 28) {
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
            onDismissRequest = {},
            shape = MaterialTheme.shapes.small,
            title = {
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

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    GameScreen()

}

@Preview(showBackground = true)
@Composable
fun ElemPreview() {
    WinDialog(text = "assad", setState = { /*TODO*/ }){}
}


