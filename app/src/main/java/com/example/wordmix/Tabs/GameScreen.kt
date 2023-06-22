package com.example.android.unscramble.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wordmix.GameViewModel
import com.example.wordmix.Tabs.*
import com.example.wordmix.ui.theme.WordMixTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController


@SuppressLint("UnrememberedMutableState")
@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = viewModel()
) {

    val gameUiState by viewModel._uiState
    WordMixTheme(darkTheme = false){
        when(gameUiState.tab){
            0 -> {
                HomeTab(viewModel = viewModel)
            }
            1 -> {
                GameTab(viewModel = viewModel,
                        gameUiState = gameUiState)
            }
            2 -> {
                LoginTab(viewModel = viewModel)
            }
            3 -> {
                LoginedTab(viewModel = viewModel,
                    gameUiState = gameUiState)
            }
            4 -> {
                LeaderBoardTab(viewModel = viewModel,
                    gameUiState = gameUiState)
            }
            5 -> {
                RegisterTab(viewModel = viewModel)
            }
        }

        SystemUIColor(color = MaterialTheme.colors.primary)
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
        Text(text = text,
             color = Color.White)
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


