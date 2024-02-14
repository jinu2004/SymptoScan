package com.jinu.imagelabel.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.Cameraswitch
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.jinu.imagelabel.mvvm.MainViewModel
import com.jinu.imagelabel.navigation.Screens
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CameraScreen(private val navController: NavController, private val viewModel: MainViewModel) {
    @Composable
    fun View() {
        val localContext = LocalContext.current.applicationContext
        val controller = remember {
            LifecycleCameraController(localContext).apply {
                setEnabledUseCases(CameraController.IMAGE_ANALYSIS or CameraController.IMAGE_CAPTURE)
            }
        }
        var isDialogOpen by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxSize()) {
            CameraPreview(controller = controller)
            IconButton(
                onClick = {
                    controller.cameraSelector =
                        if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        } else CameraSelector.DEFAULT_BACK_CAMERA
                },
                modifier = Modifier.offset(16.dp, 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Cameraswitch,
                    contentDescription = "Switch Camera",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                IconButton(
                    onClick = {
                        isDialogOpen = takePhoto(
                            controller = controller,
                            onPhoto = {
                                viewModel.onTakePhoto(it)
                                viewModel._filePath.value = storeImageInTempFile(it)!!
                            },
                            localContext
                        )
                        isDialogOpen = true
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier
                        .clip(RoundedCornerShape(100))
                        .width(100.dp)
                        .height(100.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Take photo",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            OutlinedCard(
                modifier = Modifier
                    .width(320.dp)
                    .height(320.dp)
                    .align(Alignment.Center),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                border = BorderStroke(5.dp, color = MaterialTheme.colorScheme.primary)
            ) {

            }

        }


    }


    @Composable
    private fun CameraPreview(controller: LifecycleCameraController) {
        val lifecycleOwner = LocalLifecycleOwner.current


        AndroidView(factory = {
            PreviewView(it).apply {
                this.controller = controller
                controller.bindToLifecycle(lifecycleOwner)
            }
        }, modifier = Modifier.fillMaxSize()) {
            it.onDrawForeground(android.graphics.Canvas())
        }
    }

    private fun takePhoto(
        controller: LifecycleCameraController,
        onPhoto: (Bitmap) -> Unit,
        context: Context,
    ): Boolean {
        controller.takePicture(ContextCompat.getMainExecutor(context),
            object : OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    val matrix = Matrix().apply {
                        postRotate(image.imageInfo.rotationDegrees.toFloat())
                    }
                    val rotatedBitmap = Bitmap.createBitmap(
                        image.toBitmap(),
                        0,
                        0,
                        image.width,
                        image.height,
                        matrix,
                        true
                    )
                    onPhoto(rotatedBitmap)
                    navController.navigate(Screens.HomeScreen.route)


                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.e("Camera", "Couldn't take photo: ", exception)
                }
            })
        return true
    }

    private fun storeImageInTempFile(bitmap: Bitmap): String? {
        var tempFile: File? = null
        try {
            tempFile = File.createTempFile("temp_image", ".jpg")
            val outputStream = FileOutputStream(tempFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return tempFile?.path
    }
}
