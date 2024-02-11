package com.jinu.imagelabel.classification

import android.graphics.Bitmap


data class ClassificationResult(val name: String, val score: Float, val boundingBox:Box)
