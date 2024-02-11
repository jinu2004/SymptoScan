package com.jinu.imagelabel.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.jinu.imagelabel.mvvm.MainViewModel
import com.jinu.imagelabel.ui.theme.ImageLabelTheme

class ResultScreen(private val navController: NavController) {
    @Composable
    fun View(){
        Column {
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .fillMaxHeight(0.5f)
            ){

            }
        }


    }











    @Composable
    @Preview
    fun JetKiteButtonPreview() {
        ImageLabelTheme {
            Surface {
                View()
            }

        }
    }

}





