package com.jinu.imagelabel.classification

sealed class Model(val route: String , val path:String) {
    object BrainTumor:Model("Brain Tumor","brain-tumor-detector-model.tflite")
    object Retina:Model("Retina","eye_disease.tflite")
}
