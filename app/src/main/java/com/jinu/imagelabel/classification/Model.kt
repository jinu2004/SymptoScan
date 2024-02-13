package com.jinu.imagelabel.classification

sealed class Model(val route: String) {
    object BrainTumor:Model("Brain Tumor")
    object BoneFracture:Model("Bone Fracture")
}
