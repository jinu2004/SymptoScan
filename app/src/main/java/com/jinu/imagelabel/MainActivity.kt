package com.jinu.imagelabel

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.jinu.imagelabel.mvvm.MainViewModel
import com.jinu.imagelabel.navigation.Navigation
import com.jinu.imagelabel.ui.theme.ImageLabelTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val permission = rememberMultiplePermissionsState(permissions = listOf(Manifest.permission.CAMERA))
            val lifecycleOwner = LocalLifecycleOwner.current
            val navController = rememberNavController()
            val viewModel = viewModel<MainViewModel>()

            ImageLabelTheme {
                DisposableEffect(key1 = lifecycleOwner, effect = {
                    val observer = LifecycleEventObserver { _, event ->
                        if(event == Lifecycle.Event.ON_START) {
                            permission.launchMultiplePermissionRequest()
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)

                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                })
                    permission.permissions.forEach {
                        when(it.permission){
                            Manifest.permission.MANAGE_EXTERNAL_STORAGE ->{
                                when {
                                    it.hasPermission -> {
                                        Text(text = "Camera permission accepted")
                                    }
                                    it.shouldShowRationale -> {
                                        Text(text = "Camera permission is needed" +
                                                "to access the camera")
                                    }
                                }
                            }

                            else -> {}
                        }
                    }

                    Navigation(navController = navController,viewModel)
            }
        }
    }

}



