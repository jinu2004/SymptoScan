package com.jinu.imagelabel.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.graphics.toRect
import androidx.navigation.NavController
import com.jinu.imagelabel.classification.ModelClassifier
import com.jinu.imagelabel.mvvm.MainViewModel
import com.jinu.imagelabel.ui.theme.ImageLabelTheme
import java.io.File

class ResultScreen(private val navController: NavController, private val viewModel: MainViewModel) {
    @Composable
    fun View() {
        Column {
            val context = LocalContext.current.applicationContext
            Card(modifier = Modifier.wrapContentSize()) {
                val bitmap = fileToBitmap(File(viewModel._filePath.value))
                bitmap?.let { bitmap1 ->
                    val result = ModelClassifier(context).classify(bitmap1)
                    val mutable = bitmap1.copy(Bitmap.Config.ARGB_8888, true)
                    val canvas = Canvas(mutable)
                    val h = mutable.height
                    val w = mutable.width
                    val paint = Paint()
                    paint.textSize = h / 15f
                    paint.strokeWidth = h / 85f
                    var x = 0
                    result.forEachIndexed { _, classificationResult ->
                        paint.color = Color.Green.toArgb()
                        paint.style = Paint.Style.STROKE
                        val point = classificationResult.boundingBox
                        val rect = RectF(point.left, point.top, point.right, point.right).toRect()
                        canvas.drawRect(rect, paint)
                        paint.color = Color.Red.toArgb()
                        paint.style = Paint.Style.FILL
                        canvas.drawText(
                            classificationResult.name,
                            point.left,
                            point.top - 20,
                            paint
                        )
                    }

                    Image(
                        bitmap = mutable.asImageBitmap(),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.wrapContentSize()
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


}





