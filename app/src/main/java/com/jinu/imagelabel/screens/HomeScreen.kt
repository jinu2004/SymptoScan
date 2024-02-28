package com.jinu.imagelabel.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.jinu.imagelabel.R
import com.jinu.imagelabel.classification.Model
import com.jinu.imagelabel.mvvm.MainViewModel
import com.jinu.imagelabel.navigation.Screens
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class HomeScreen(private val navController: NavController, private val viewModel: MainViewModel) {
    private var selectedImage: ImageBitmap? by mutableStateOf(null)
    private var tempFile: File? = null

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun View() {

        var model by remember {
            mutableStateOf(Model.BrainTumor.route)
        }
        var threshold by remember {
            mutableDoubleStateOf(0.5)
        }
        var maxValue by remember {
            mutableIntStateOf(1)
        }
        var pathFile by remember {
            mutableStateOf("")
        }

        val localContext = LocalContext.current.applicationContext


        val modelList = listOf(Model.BrainTumor.route, Model.Retina.route)

        var dropDownState by remember {
            mutableStateOf(false)
        }
        LocalContext.current
        val imageCropLauncher =
            rememberLauncherForActivityResult(contract = CropImageContract()) { result ->
                if (result.isSuccessful) {
                    result.uriContent?.let {
                        val bitmap = if (Build.VERSION.SDK_INT < 28) {
                            MediaStore.Images
                                .Media.getBitmap(localContext.contentResolver, it)
                        } else {
                            val source = ImageDecoder
                                .createSource(localContext.contentResolver, it)
                            ImageDecoder.decodeBitmap(source)
                        }

                        viewModel._filePath.value = storeImageInTempFile(bitmap)!!
                    }

                } else {
                    println("ImageCropping error: ${result.error}")
                }
            }


        Log.e("file", viewModel._filePath.value)



        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { TopBar() },
            bottomBar = {}
        ) {
            it.calculateTopPadding()


            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = it.calculateTopPadding()),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item {
                    Text(
                        text =
                        "Empowering Doctors with Precision: Your MRI and X-Ray Symptom Navigator",
                        modifier = Modifier.padding(20.dp),
                        fontFamily = FontFamily(Font(resId = R.font.roboto_mono_medium)),
                        fontWeight = FontWeight(1000),
                        fontStyle = FontStyle.Normal,
                        fontSize = TextUnit(14f, TextUnitType.Sp)
                    )
                }
                item {
                    Column {

                        OutlinedTextField(
                            value = model,
                            onValueChange = { string -> model = string },
                            label = { Text(text = "Select Model") },
                            placeholder = {
                                Text(
                                    text = model, letterSpacing = TextUnit(
                                        10f,
                                        TextUnitType.Sp
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusEvent { focus ->
                                    if (focus.isFocused) dropDownState = true
                                }
                                .padding(start = 20.dp, end = 20.dp),
                            trailingIcon = {
                                IconButton(onClick = { dropDownState = true }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null
                                    )
                                }
                            },
                            singleLine = true,
                            readOnly = true,
                        )
                        DropdownMenu(
                            expanded = dropDownState,
                            onDismissRequest = { dropDownState = false },
                            offset = DpOffset(30.dp, 20.dp)
                        ) {
                            modelList.forEachIndexed { _, s ->
                                DropdownMenuItem(
                                    text = { Text(text = s) },
                                    onClick = { model = s;dropDownState = false },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                }
                item {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        OutlinedTextField(
                            value = threshold.toString(),
                            onValueChange = { string -> threshold = string.toDouble() },
                            label = { Text(text = "Enter min Score") },
                            placeholder = {
                                Text(
                                    text = model, letterSpacing = TextUnit(
                                        10f,
                                        TextUnitType.Sp
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .padding(start = 20.dp, end = 20.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = maxValue.toString(),
                            onValueChange = { string -> maxValue = string.toInt() },
                            label = { Text(text = "Enter Max Result") },
                            placeholder = {
                                Text(
                                    text = model, letterSpacing = TextUnit(
                                        10f,
                                        TextUnitType.Sp
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 20.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            enabled = true
                        )
                    }


                }
                item {
                    OutlinedCard(
                        modifier = Modifier
                            .width(320.dp)
                            .height(320.dp),
                        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
                        border = BorderStroke(
                            5.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                        ) {
                            if (File(viewModel._filePath.value).exists()) {
                                Log.e("filepath", viewModel._filePath.value)
                                fileToBitmap(File(viewModel._filePath.value))?.let { it1 ->
                                    Image(
                                        bitmap = it1.asImageBitmap(),
                                        contentDescription = "",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(),
                                        contentScale = ContentScale.FillBounds
                                    )
                                    IconButton(
                                        onClick = {
                                            File(viewModel._filePath.value).delete();viewModel._filePath.value =
                                            ""
                                        },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "None"
                                        )
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight()
                                ) {
                                    Text(
                                        text = "Select a Image", modifier = Modifier
                                            .align(
                                                Alignment.TopCenter
                                            )
                                            .padding(top = 30.dp)
                                    )
                                    Row(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .padding(50.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                                    ) {
                                        IconButton(
                                            onClick = {
                                                val cropOptions = CropImageContractOptions(
                                                    null,
                                                    CropImageOptions(
                                                        imageSourceIncludeGallery = false,
                                                        imageSourceIncludeCamera = true,
                                                        cropShape = CropImageView.CropShape.RECTANGLE,
                                                        minCropResultWidth = 640,
                                                        minCropResultHeight = 640,
                                                    )
                                                )
                                                imageCropLauncher.launch(cropOptions)
                                            },
                                            modifier = Modifier
                                                .size(70.dp)
                                                .clip(RoundedCornerShape(100)),
                                            colors = IconButtonDefaults.filledIconButtonColors(
                                                containerColor = MaterialTheme.colorScheme.primaryContainer
                                            )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CameraAlt,
                                                contentDescription = ""
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                val cropOptions = CropImageContractOptions(
                                                    null,
                                                    CropImageOptions(
                                                        imageSourceIncludeGallery = true,
                                                        imageSourceIncludeCamera = false,
                                                        cropShape = CropImageView.CropShape.RECTANGLE,
                                                        minCropResultWidth = 640,
                                                        minCropResultHeight = 640,
                                                    )
                                                )
                                                imageCropLauncher.launch(cropOptions)
                                            },
                                            modifier = Modifier
                                                .size(70.dp)
                                                .clip(RoundedCornerShape(100)),
                                            colors = IconButtonDefaults.filledIconButtonColors(
                                                containerColor = MaterialTheme.colorScheme.primaryContainer
                                            )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.FileOpen,
                                                contentDescription = ""
                                            )
                                        }
                                    }
                                }


                            }

                        }
                    }
                }
                item {

                    Button(onClick = {
                        when (model) {
                            Model.BrainTumor.route -> navController.navigate(
                                Screens.ResultScreen.withArgs(
                                    Model.BrainTumor.path,
                                    threshold.toString(),
                                    maxValue.toString()
                                )
                            )

                            Model.Retina.route -> navController.navigate(
                                Screens.ResultScreen.withArgs(
                                    Model.Retina.path,
                                    threshold.toString(),
                                    maxValue.toString()
                                )
                            )

                            else -> navController.navigate(
                                Screens.ResultScreen.withArgs(
                                    Model.BrainTumor.path,
                                    threshold.toString(),
                                    maxValue.toString()
                                )
                            )
                        }


                    }) {
                        Text(text = "Submit")
                    }
                }

            }

        }

    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun TopBar() {
        TopAppBar(
            title = {
                Text(
                    text = "SymptoScan",
                    modifier = Modifier,
                    fontFamily = FontFamily(Font(resId = R.font.roboto_mono_thin)),
                    fontWeight = FontWeight(1000),
                    fontStyle = FontStyle.Normal,
                    fontSize = TextUnit(30f, TextUnitType.Sp)
                )


            },

            )
    }

    private fun fileToBitmap(file: File): Bitmap? {
        return try {
            BitmapFactory.decodeFile(file.absolutePath)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("errorfrom", e.printStackTrace().toString())
            null
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

}