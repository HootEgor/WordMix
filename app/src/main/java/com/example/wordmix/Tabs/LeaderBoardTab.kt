package com.example.wordmix.Tabs

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android.unscramble.ui.CustomFloatingButton
import com.example.wordmix.GameViewModel
import com.example.wordmix.ui.theme.GameUiState

@Composable
fun LeaderBoardTab(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel,
    gameUiState: GameUiState
) {
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
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                border = BorderStroke(2.dp, MaterialTheme.colors.primaryVariant),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.09f),
            ){
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ){
                    for(i in 0 until 3){
                        LanguageButton(setBoarder = gameUiState.leaderBoardLanguage == i,
                            text = viewModel.languageText(i),
                            press = {viewModel.setLeaderBoard(i)})
                    }
                }
            }

            Surface(
                border = BorderStroke(2.dp, MaterialTheme.colors.primaryVariant),
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
                    for((n, cell) in viewModel.getLeaderBoard().withIndex()){
                        Log.d("EEE", "${cell}")
                        ScoreCell(
                            index = n+1,
                            userName = cell.userName,
                            score = cell.score.toString()
                        )
                        Divider()
                    }

                }
            }

        }
    }
}

@Composable
fun LanguageButton(
    setBoarder: Boolean,
    text: String,
    press: () -> Unit
){
    var border = if (setBoarder) BorderStroke(2.dp, MaterialTheme.colors.secondary)
    else null
    Button(
        onClick = press,
        border = border,
        shape = RoundedCornerShape(32),
        colors= ButtonDefaults.buttonColors(MaterialTheme.colors.primaryVariant),
        modifier = Modifier
            .height(50.dp)
            .width(110.dp)
    ){
        Text(text = text,
            fontSize = 12.sp,)
    }
}

@Composable
fun ScoreCell(
    index: Int,
    userName: String,
    score: String
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
            Text(text = userName,
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
            Text(text = score,
                fontSize = 20.sp,
                modifier = Modifier
                    .wrapContentSize()
            )
        }
    }
}
