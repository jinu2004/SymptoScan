package com.jinu.imagelabel.classification

import com.jinu.imagelabel.classification.Box


data class ClassificationResult(val name: String, val score: Float, val boundingBox: Box)
