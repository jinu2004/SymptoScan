package com.jinu.imagelabel.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.Cameraswitch
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jinu.imagelabel.classification.ClassificationResult
import com.jinu.imagelabel.mvvm.MainViewModel
import com.jinu.imagelabel.navigation.Screens
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CameraScreen(private val navController: NavController) {
    val detectionResult = arrayListOf<ClassificationResult>()

    @Composable
    fun View() {
        val scope = rememberCoroutineScope()
        val localContext = LocalContext.current.applicationContext
        val controller = remember {
            LifecycleCameraController(localContext).apply {
                setEnabledUseCases(CameraController.IMAGE_ANALYSIS or CameraController.IMAGE_CAPTURE)
            }
        }
        val viewModel = viewModel<MainViewModel>()
        var isDialogOpen by remember { mutableStateOf(false) }

        var path by remember {
            mutableStateOf("")
        }






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
                                path = storeImageInTempFile(it)!!
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

                    if (isDialogOpen && File(path).exists())
                        DialogWithImage(
                            onDismissRequest = { isDialogOpen = false; },
                            onConfirmation = {
                                isDialogOpen = false; navController.navigate(Screens.ResultScreen.route)
                            },
                            bitmap = fileToBitmap(File(path))?.asImageBitmap()!!,
                        )
                }
        Log.e("isOpen", File("temp_image.jpg").isFile.toString())


        }



    @Composable
    private fun CameraPreview(controller: LifecycleCameraController) {
        val lifecycleOwner = LocalLifecycleOwner.current
        AndroidView(factory = {
            PreviewView(it).apply {
                this.controller = controller
                controller.bindToLifecycle(lifecycleOwner)
            }
        }, modifier = Modifier.fillMaxSize())
    }

    private fun takePhoto(
        controller: LifecycleCameraController,
        onPhoto: (Bitmap) -> Unit,
        context: Context,
    ):Boolean {
        var isSuccess = false
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
                    isSuccess = true


                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.e("Camera", "Couldn't take photo: ", exception)
                }
            })
        return isSuccess
    }

    @Composable
    private fun DialogWithImage(
        onDismissRequest: () -> Unit,
        onConfirmation: () -> Unit,
        bitmap: ImageBitmap,
    ) {
        Dialog(onDismissRequest = { onDismissRequest() }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(375.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(Modifier.size(200.dp).padding(10.dp)) {
                        Image(
                            bitmap = bitmap, contentDescription = "",
                            modifier = Modifier
                                .fillMaxSize(),
                            contentScale = ContentScale.FillBounds
                        )
                    }

                    Text(
                        text = "This is a the image taken its just a testing .",
                        modifier = Modifier.padding(16.dp),
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        TextButton(
                            onClick = { onDismissRequest() },
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text("Dismiss")
                        }
                        TextButton(
                            onClick = { onConfirmation() },
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
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

    private fun fileToBitmap(file: File): Bitmap? {
        return try {
            BitmapFactory.decodeFile(file.absolutePath)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("errorfrom",e.printStackTrace().toString())
            null
        }
    }
}
