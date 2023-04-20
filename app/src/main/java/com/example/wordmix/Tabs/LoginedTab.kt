package com.example.wordmix.Tabs

import android.annotation.SuppressLint
import android.media.Image
import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import com.example.android.unscramble.ui.CustomFloatingButton
import com.example.wordmix.GameViewModel
import com.example.wordmix.R
import com.example.wordmix.ui.theme.GameUiState
import com.example.wordmix.ui.theme.ScoreCell
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder

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
                    press = {viewModel.logoutUser()},
                    size = 50,
                    icon = Icons.Filled.ExitToApp)//imageResource(id = R.drawable.baseline_logout_24))
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
                if (gameUiState.creatingLeaderBoard){
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        CircularProgressIndicator()
                    }
                }else{
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .weight(1f, false)
                            .fillMaxSize()
                    ) {
                        val board = gameUiState.userHistory
                        if (board != null) {
                            for((n, cell) in board.withIndex()){
                                cell.let {
                                    HistoryScoreCell(
                                        index = n+1,
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
    val roundedValue = 0.dp
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(rowHeight)
    ){
        Surface(
            color= color,
            shape = RoundedCornerShape(roundedValue),
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
            shape = RoundedCornerShape(roundedValue),
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
            shape = RoundedCornerShape(roundedValue),
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