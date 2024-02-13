package com.jinu.imagelabel.classification

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.BaseOptions.DelegateOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetector
import com.jinu.finddiseaseai.classification.ModelClassifierInterface
import com.jinu.imagelabel.ui.theme.items.centerCrop

class ModelClassifier(
    context: Context,
    threshold: Float = 0.5f,
    maxResult: Int = 1,
    modelPath:String
) : ModelClassifierInterface {

    private val options: ObjectDetector.ObjectDetectorOptions =
        ObjectDetector.ObjectDetectorOptions.builder()
            .setBaseOptions(BaseOptions.builder().setModelAssetPath(modelPath).build())
            .setRunningMode(RunningMode.IMAGE)
            .setMaxResults(maxResult)
            .setScoreThreshold(threshold)
            .build();
    private val objectDetector: ObjectDetector = ObjectDetector.createFromOptions(context, options);

    override fun classify(bitmap: Bitmap): List<ClassificationResult> {

        val image = bitmap.centerCrop(640,640)
        val mpImage = BitmapImageBuilder(image).build()
        val result = objectDetector.detect(mpImage)

        return result.detections().flatMap {
            it.categories().map { category ->
                Log.i("test", category.categoryName())
                ClassificationResult(
                    name = category.categoryName(),
                    score = category.score(),
                    boundingBox = Box(it.boundingBox().left, it.boundingBox().top,it.boundingBox().right,it.boundingBox().right)
                )

            }
        }.distinctBy { it.name }

    }

}