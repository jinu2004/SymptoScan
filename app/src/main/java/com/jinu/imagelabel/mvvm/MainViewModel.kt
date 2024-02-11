package com.jinu.imagelabel.mvvm

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.jinu.imagelabel.classification.ClassificationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel:ViewModel() {
    private val _bitmaps = MutableStateFlow<List<Bitmap>>(emptyList())
    val bitmaps = _bitmaps.asStateFlow()

    private val _detectionResult = MutableStateFlow<List<ClassificationResult>>(emptyList())
    val detectionResult = _detectionResult.asStateFlow()



    fun onTakePhoto(bitmap: Bitmap) {
        _bitmaps.value += bitmap
    }
}