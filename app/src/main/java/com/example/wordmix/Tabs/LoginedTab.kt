package com.example.wordmix.Tabs

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android.unscramble.ui.CustomFloatingButton
import com.example.wordmix.GameViewModel
import com.example.wordmix.ui.theme.GameUiState
import com.example.wordmix.ui.theme.ScoreCell

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun LoginedTab(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel,
    gameUiState: GameUiState
) {
    Scaffold(
        topBar = {
            TopAppBar{
                CustomFloatingButton(
                    press = {viewModel.setTab(2)},
                    size = 50,
                    icon = Icons.Default.ExitToApp)
            }
        },
        bottomBar = {
            BottomAppBar(
                cutoutShape = MaterialTheme.shapes.small.copy(
                    CornerSize(percent = 50)
                )) { /* Bottom app bar content */ }
        },
        floatingActionButton = {
            CustomFloatingButton(
            press = {viewModel.setTab(0)},
            size = 60,
            icon = Icons.Default.Home)
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center
    ){
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Your history:",
                style = TextStyle(fontSize = 40.sp, fontFamily = FontFamily.Default)
            )
            Surface(
                border = BorderStroke(2.dp, Color.Gray),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f),
            ){
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .weight(1f, false)
                        .fillMaxSize()
                ) {

                    if(gameUiState.userHistory != null){
                        var n = 0
                        for(i in 0 until 20){

                            for(cell in gameUiState.userHistory!!){
                                n++
                                cell.let {
                                    HistoryScoreCell(
                                        index = n,
                                        cell = it,
                                        laguageText = viewModel.languageText(it.Language))
                                }
                                Divider()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryScoreCell(
    index: Int,
    cell: ScoreCell,
    laguageText: String
){
    val color = Color.LightGray
    val rowHeight = 50.dp
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(rowHeight)
    ){
        Surface(
            color= color,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxHeight()
                .width(rowHeight)
        ){
            Text(text = index.toString(),
                fontSize = 20.sp,
                modifier = Modifier
                    .wrapContentSize()
            )
        }

        Surface(
            color= color,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.5f),
        ){
            Text(text = laguageText,
                fontSize = 20.sp,
                modifier = Modifier
                    .wrapContentSize()
            )
        }

        Surface(
            color= color,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(),
        ){
            Text(text = cell.Score.toString(),
                fontSize = 20.sp,
                modifier = Modifier
                    .wrapContentSize()
            )
        }
    }
}