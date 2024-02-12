package com.jinu.imagelabel.classification

import android.graphics.Bitmap

interface ModelClassifierInterface {
    fun classify(bitmap: Bitmap):List<ClassificationResult>
}