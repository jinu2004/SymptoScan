package com.jinu.imagelabel.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavArgs
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jinu.imagelabel.screens.CameraScreen
import com.jinu.imagelabel.screens.HomeScreen
import com.jinu.imagelabel.screens.ResultScreen


@Composable
fun Navigation(navController:NavHostController){
    NavHost(navController = navController , startDestination = Screens.CameraScreen.route){
        composable(Screens.HomeScreen.route){
            HomeScreen(navController).View()
        }
        composable(Screens.CameraScreen.route){
            CameraScreen(navController).View()
        }
        composable(Screens.ResultScreen.route){
            ResultScreen(navController).View()
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