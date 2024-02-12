package com.jinu.finddiseaseai.classification

import android.graphics.Bitmap
import com.jinu.imagelabel.classification.ClassificationResult

interface ModelClassifierInterface {
    fun classify(bitmap: Bitmap):List<ClassificationResult>
}