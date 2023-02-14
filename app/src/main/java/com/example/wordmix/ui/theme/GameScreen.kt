package com.example.android.unscramble.ui

import android.R.attr.fontFamily
import androidx.compose.ui.text.font.FontFamily
import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.TextStyle.Companion.Default
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.wordmix.GameViewModel
import com.example.wordmix.R
import com.example.wordmix.ui.theme.Tile
import com.example.wordmix.ui.theme.WordMixTheme
import com.example.wordmix.ui.theme.border
import com.example.wordmix.ui.theme.color
import com.google.accompanist.systemuicontroller.rememberSystemUiController


@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = viewModel()
) {


    val gameUiState by viewModel._uiState
    val backManager by viewModel.backManager

    when(gameUiState.tab){
        0 -> {
            Scaffold(
                topBar = {
                    TopAppBar{
                        CustomFloatingButton(
                            press = {viewModel.setTab(2)},
                            size = 50,
                            icon = Icons.Default.AccountCircle)
                    }
                },
                bottomBar = {
                    BottomAppBar(
                        cutoutShape = MaterialTheme.shapes.small.copy(
                            CornerSize(percent = 50)
                    )) { /* Bottom app bar content */ }
                },
                floatingActionButton = {CustomFloatingButton(
                                                press = {},
                                                size = 80,
                                                icon = Icons.Default.Star)},
                isFloatingActionButtonDocked = true,
                floatingActionButtonPosition = FabPosition.Center
            ) {
                Column(modifier = Modifier
                    .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.2f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Оберіть мову:",
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center
                        )

                        var expanded by remember { mutableStateOf(false) }

                        Box {
                            Text(text = viewModel.currentLanguageText(),
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .clickable(onClick = { expanded = !expanded })
                            )
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                CustomText(text = viewModel.languageText(0),
                                    press = {
                                        expanded = !expanded
                                        viewModel.setLanguage(0)
                                    })
                                Divider()
                                CustomText(text = viewModel.languageText(1),
                                    press = {
                                        expanded = !expanded
                                        viewModel.setLanguage(1)
                                    })
                                Divider()
                                CustomText(text = viewModel.languageText(2),
                                    press = {
                                        expanded = !expanded
                                        viewModel.setLanguage(2)
                                    })
                            }
                        }
                    }

                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.5f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CustomButton(enable = true,
                            text = "Почати",
                            press = { viewModel.setTab(1) })
                        CustomButton(enable = true,
                            text = "test",
                            press = { backManager.test()})
                    }


                }
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
                        text = "Назад",
                        press = { viewModel.setTab(0)})

                }


            }

            if(gameUiState.showWinDialog){
                WinDialog(text = viewModel.winDialogText(),
                    setState = {viewModel.setWinDailog(false)},
                    nextRound = { viewModel.nextRound() })
            }

            if(gameUiState.editMode){
                EditDialog(action = {viewModel.unBlockWord()},
                    setState = { viewModel.editMode()})
            }

        }
        2 -> {
            Scaffold(
                topBar = {
                    TopAppBar{}
                },
                bottomBar = {
                    BottomAppBar(
                        cutoutShape = MaterialTheme.shapes.small.copy(
                            CornerSize(percent = 50)
                        )) { /* Bottom app bar content */ }
                },
                floatingActionButton = {CustomFloatingButton(
                    press = {viewModel.setTab(0)},
                    size = 60,
                    icon = Icons.Default.Home)},
                isFloatingActionButtonDocked = true,
                floatingActionButtonPosition = FabPosition.Center
            ){
                Box(modifier = Modifier.fillMaxSize()) {
                    ClickableText(
                        text = AnnotatedString("Signup Here"),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(20.dp),
                        onClick = {},
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Default,
                            textDecoration = TextDecoration.Underline,
                            color = MaterialTheme.colors.primaryVariant
                        )
                    )
                }
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val username = remember {
                        mutableStateOf(TextFieldValue())
                    }
                    val password = remember {
                        mutableStateOf(TextFieldValue())
                    }

                    Text(
                        text = "Login",
                        style = TextStyle(fontSize = 40.sp, fontFamily = FontFamily.Cursive)
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    TextField(
                        label = { Text(text = "Username") },
                        value = username.value,
                        onValueChange = { username.value = it }
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    TextField(
                        label = { Text(text = "Password") },
                        value = password.value,
                        onValueChange = { password.value = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                        Button(
                            onClick = {},
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(text = "Login")
                        }
                    }

                    Spacer(modifier = Modifier.height(15.dp))
                }
            }
        }
    }

    SystemUIColor(color = MaterialTheme.colors.primary)
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
fun CustomFloatingButton(
    press: () -> Unit,
    size: Int,
    icon: ImageVector
){

    FloatingActionButton(
        onClick = press,
        modifier = Modifier.size(size.dp),
        backgroundColor = MaterialTheme.colors.secondary
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "FAB",
            tint = Color.White,
            modifier = Modifier.fillMaxSize(0.7f)
        )
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
            .clickable(onClick = press)
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

@Composable
fun SystemUIColor(
    color: Color
){
    val systemUiController = rememberSystemUiController()
    systemUiController.setNavigationBarColor(
        color = color,
        darkIcons = false
    )

    systemUiController.setSystemBarsColor(
        color = color,
        darkIcons = false
    )
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


