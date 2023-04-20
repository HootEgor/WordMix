package com.example.wordmix.Tabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android.unscramble.ui.CustomButton
import com.example.android.unscramble.ui.CustomFloatingButton
import com.example.wordmix.GameViewModel

@Composable
fun HomeTab(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel
) {
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
        floatingActionButton = {
            CustomFloatingButton(
            press = {viewModel.switchToLeaderBoard()},
            size = 80,
            icon = Icons.Default.Star)
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center
    ) {
        Column(modifier = Modifier
            .fillMaxSize().padding(it),
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
            }


        }
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