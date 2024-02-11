package com.jinu.imagelabel.classification

import android.graphics.Bitmap

interface ModelClassifierInterface {
    fun classify(bitmap: Bitmap,rotation:Int):List<ClassificationResult>
}