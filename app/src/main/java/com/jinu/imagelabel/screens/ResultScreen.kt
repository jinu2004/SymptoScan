package com.jinu.imagelabel.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jinu.imagelabel.R
import com.jinu.imagelabel.classification.Model
import com.jinu.imagelabel.classification.ModelClassifier
import com.jinu.imagelabel.mvvm.MainViewModel
import com.jinu.imagelabel.ui.theme.items.centerCrop
import java.io.File

class ResultScreen(
    private val navController: NavController,
    private val viewModel: MainViewModel,
    private val model: String?,
    private val threshold: String?,
    private val maxResult:String?
) {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun View() {
        Scaffold(topBar = { TopBar() }) {
            it.calculateTopPadding()
            Column {
                val context = LocalContext.current.applicationContext
                val listOfColors = listOf(
                    Color.Red, Color.Green, Color.Blue, Color.Magenta,
                    Color.Yellow
                )
                val bitmap = fileToBitmap(File(viewModel._filePath.value))




                bitmap?.let { bitmap1 ->
                    val result =
                        ModelClassifier(context = context, modelPath = model!!).classify(
                            bitmap1
                        )
                    val mutable = bitmap1.centerCrop(640, 640).copy(Bitmap.Config.ARGB_8888, true)
                    val canvas = Canvas(mutable)
                    val h = mutable.height
                    val w = mutable.width
                    val paint = Paint()
                    paint.textSize = h / 15f
                    paint.strokeWidth = h / 85f
                    var x = 0
                    result.forEachIndexed { index, classificationResult ->
                        paint.color = listOfColors[index].toArgb()
                        paint.style = Paint.Style.STROKE
                        val point = classificationResult.boundingBox
                        val rect = Rect(
                            point.left.toInt(),
                            point.top.toInt(),
                            point.right.toInt(),
                            point.right.toInt()
                        )
                        canvas.drawRect(rect, paint)
                        paint.color = Color.Red.toArgb()
                        paint.style = Paint.Style.FILL
                        canvas.drawText(
                            classificationResult.name,
                            point.left,
                            point.top,
                            paint
                        )
                    }

                    Image(
                        bitmap = mutable.asImageBitmap(),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.width(640.dp).height(640.dp)
                    )
                }

            }
        }


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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun TopBar() {
        TopAppBar(
            title = {
                Text(
                    text = "Results",
                    modifier = Modifier,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontFamily = FontFamily(Font(resId = R.font.trochut_bold)),
                    fontWeight = FontWeight(1000),
                    fontStyle = FontStyle.Normal,
                    fontSize = TextUnit(30f, TextUnitType.Sp)
                )
            })
    }


}





