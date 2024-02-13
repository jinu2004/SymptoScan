package com.jinu.imagelabel.screens

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentCompositionLocalContext
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
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.jinu.imagelabel.MainActivity
import com.jinu.imagelabel.R
import com.jinu.imagelabel.classification.Model
import com.jinu.imagelabel.mvvm.MainViewModel
import java.io.File

class HomeScreen(private val navController: NavController, private val viewModel: MainViewModel) {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun View() {

        var model by remember {
            mutableStateOf("Brain Tumor")
        }
        var threshold by remember {
            mutableDoubleStateOf(0.5)
        }
        var maxValue by remember {
            mutableIntStateOf(5)
        }
        var pathFile by remember {
            mutableStateOf("")
        }

        val modelList = listOf(Model.BrainTumor.route, Model.BoneFracture.route)

        var dropDownState by remember {
            mutableStateOf(false)
        }
        val context = LocalContext.current
        val contentLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { uri: Uri? ->
                uri?.let {
                    pathFile = it.path.toString()
                }
            }
        )



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
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
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
                                .padding(top = 30.dp, start = 20.dp, end = 20.dp),
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
                            modelList.forEachIndexed { index, s ->
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
                                .padding(top = 30.dp, start = 20.dp, end = 20.dp),
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
                                .padding(top = 30.dp, start = 20.dp, end = 20.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }


                }
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                            .wrapContentHeight()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                        ) {
                            if (File(viewModel._filePath.value).exists()) {
                                fileToBitmap(File(viewModel._filePath.value))?.let { it1 ->
                                    Image(
                                        bitmap = it1.asImageBitmap(),
                                        contentDescription = "",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                            } else {
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(50.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                                ) {
                                    IconButton(
                                        onClick = { /*TODO*/ },
                                        modifier = Modifier
                                            .size(70.dp)
                                            .clip(RoundedCornerShape(100)),
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Camera,
                                            contentDescription = ""
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            contentLauncher.launch("image/*")
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

        }

    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun TopBar() {
        TopAppBar(
            title = {
                Text(
                    text = "FindDiseaseAi",
                    modifier = Modifier,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontFamily = FontFamily(Font(resId = R.font.trochut_bold)),
                    fontWeight = FontWeight(1000),
                    fontStyle = FontStyle.Normal,
                    fontSize = TextUnit(30f, TextUnitType.Sp)
                )
            })
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


}