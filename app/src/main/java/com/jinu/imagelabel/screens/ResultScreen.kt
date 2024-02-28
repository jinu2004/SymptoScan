package com.jinu.imagelabel.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import java.io.File

class ResultScreen(
    private val navController: NavController,
    private val viewModel: MainViewModel,
    private val model: String?,
    private val threshold: String?,
    private val maxResult: String?
) {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun View() {
        Scaffold(topBar = { TopBar() }) {
            it.calculateTopPadding()
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val context = LocalContext.current.applicationContext
                val listOfColors = listOf(
                    Color.Red, Color.Green, Color.Blue, Color.Magenta,
                    Color.Yellow
                )
                val bitmap = fileToBitmap(File(viewModel._filePath.value))

                var name by remember {
                    mutableStateOf("")
                }

                bitmap?.let { bitmap1 ->
                    val result =
                        ModelClassifier(
                            context = context,
                            modelPath = model!!,
                            threshold = threshold!!.toFloat(),
                            maxResult = if (maxResult?.isNotEmpty() == true) maxResult.toInt() else 1
                        ).classify(
                            bitmap1
                        )
                    val mutable = bitmap1.copy(Bitmap.Config.ARGB_8888, true)
                    val canvas = Canvas(mutable)
                    val h = mutable.height
                    val w = mutable.width
                    val paint = Paint()
                    paint.textSize = h / 15f
                    paint.strokeWidth = h / 85f
                    var x = 0f
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
                        paint.color = listOfColors[index].toArgb()
                        paint.style = Paint.Style.FILL


//                        canvas.drawText(
//                            classificationResult.name,
//                            point.left + 10,
//                            point.top + 10,
//                            paint
//                        )

                        x = classificationResult.score
                        name = classificationResult.name
                        Log.e("name", name)
                    }
                    Card(
                        modifier = Modifier
                            .width(300.dp)
                            .height(300.dp),
                        shape = RoundedCornerShape(10)
                    ) {
                        Image(
                            bitmap = mutable.asImageBitmap(),
                            contentDescription = "",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        var text = ""

                        when (model) {
                            Model.BrainTumor.path ->
                                if (name == "0" || name == "1") {
                                    text = "Tumor"
                                }

                            Model.Retina.path -> text = name

                        }


                        Text(text = text, modifier = Modifier.fillMaxWidth(0.5f))



                        LinearProgressIndicator(
                            progress = x, modifier = Modifier
                                .fillMaxWidth()
                                .width(20.dp)
                                .clip(RoundedCornerShape(20))
                        )
                    }


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
                    fontFamily = FontFamily(Font(resId = R.font.roboto_mono_thin)),
                    fontWeight = FontWeight(1000),
                    fontStyle = FontStyle.Normal,
                    fontSize = TextUnit(30f, TextUnitType.Sp)
                )
            })
    }


}





