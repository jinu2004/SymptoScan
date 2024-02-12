package com.jinu.imagelabel.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jinu.imagelabel.mvvm.MainViewModel
import com.jinu.imagelabel.screens.CameraScreen
import com.jinu.imagelabel.screens.HomeScreen
import com.jinu.imagelabel.screens.ResultScreen


@Composable
fun Navigation(navController:NavHostController,viewModel: MainViewModel){
    NavHost(navController = navController , startDestination = Screens.HomeScreen.route){
        composable(Screens.HomeScreen.route){
            HomeScreen(navController,viewModel).View()
        }
        composable(Screens.CameraScreen.route){
            CameraScreen(navController,viewModel).View()
        }
        composable(Screens.ResultScreen.route){
            ResultScreen(navController,viewModel).View()
        }
    }

}

sealed class Screens( val route:String){
    object HomeScreen:Screens("home_screen")
    object CameraScreen:Screens("Camera_Screen")
    object ResultScreen:Screens("Result_Screen")





    fun withArgs(vararg args: String):String{
        return buildString {
            append(route)
            args.forEach {
                append("/$it")
            }
        }
    }
}