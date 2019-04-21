package com.dronelab.posewithkotlin

import android.app.Activity
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class ImageClassifierFloatInception private constructor(
    activity: Activity,
    imageSizeX: Int,
    imageSizeY: Int,
    private val outputW: Int,
    private val outputH: Int,
    modelPath: String,
    actionPath: String,
    numBytesPerChannel: Int = 4 // a 32bit float value requires 4 bytes
) : ImageClassifier(activity, imageSizeX, imageSizeY, modelPath, actionPath, numBytesPerChannel) {

    /**
     * An array to hold inference results, to be feed into Tensorflow Lite as outputs.
     * This isn't part of the super class, because we need a primitive array here.
     */
    private val heatMapArray: Array<Array<Array<FloatArray>>> =
        Array(1) { Array(outputW) { Array(outputH) { FloatArray(14) } } }

    private val modelFileSize = 16

    var block: Array<Array<FloatArray>> = Array(1) { Array(modelFileSize){ FloatArray(28) } }
    var currentFrameIndex = 0
   // private var currentFrameIndex = 0
    private var actionOutput: Array<FloatArray> = Array(1){ FloatArray(10) }
    private var label = arrayOf("Jumping in Place", "Jumping Jacks", "Bending", "Punching (Boxing)", "Waving (Two Hands)","Waving (Right)",
        "Clapping Hands", "Throwing a Ball", "Sitting Down", "Stand Down")
    private var mMat: Mat? = null


    override fun addPixelValue(pixelValue: Int) {
        //bgr
        imgData!!.putFloat((pixelValue and 0xFF).toFloat())
        imgData!!.putFloat((pixelValue shr 8 and 0xFF).toFloat())
        imgData!!.putFloat((pixelValue shr 16 and 0xFF).toFloat())
    }

    override fun getProbability(labelIndex: Int): Float {
        //    return heatMapArray[0][labelIndex];
        return 0f
    }

    override fun setProbability(
        labelIndex: Int,
        value: Number
    ) {
        //    heatMapArray[0][labelIndex] = value.floatValue();
    }

    override fun getNormalizedProbability(labelIndex: Int): Float {
        return getProbability(labelIndex)
    }

    override fun runActionInference() : String{
        tfaction?.run(block,actionOutput)
        val maxIdx = actionOutput[0].indices.maxBy { actionOutput[0][it] } ?: -1
       // Log.i("Outout ******", maxIdx.toString())
        return label[maxIdx]
    }

    override fun runInference() : Int{
        tflite?.run(imgData!!, heatMapArray)
        if (mPrintPointArray == null)
            mPrintPointArray = Array(2) { FloatArray(14) }
        if (!CameraActivity.isOpenCVInit)
            return 0
        // Gaussian Filter 5*5
        if (mMat == null)
            mMat = Mat(outputW, outputH, CvType.CV_32F)
        val tempArray = FloatArray(outputW * outputH)
        val outTempArray = FloatArray(outputW * outputH)
        val current_frame =  FloatArray(28)
        var k = 0
        for (i in 0..13) {
            var index = 0
            for (x in 0 until outputW) {
                for (y in 0 until outputH) {
                    tempArray[index] = heatMapArray[0][y][x][i]
                    index++
                }
            }
            mMat!!.put(0, 0, tempArray)
            Imgproc.GaussianBlur(mMat!!, mMat!!, Size(5.0, 5.0), 0.0, 0.0)
            mMat!!.get(0, 0, outTempArray)
            var maxX = 0f
            var maxY = 0f
            var max = 0f
            // Find keypoint coordinate through maximum values

            for (x in 0 until outputW) {
                for (y in 0 until outputH) {
                    val center = get(x, y, outTempArray)
                    if (center > max) {
                        max = center
                        maxX = x.toFloat()
                        maxY = y.toFloat()
                    }
                }
            }
            if (max == 0f) {
                mPrintPointArray = Array(2) { FloatArray(14) }
                return 0
            }
            mPrintPointArray!![0][i] = maxX
            mPrintPointArray!![1][i] = maxY
            current_frame[k] = maxX
            k++
            current_frame[k] = maxY
            k++
        }
        if(currentFrameIndex < modelFileSize){
            block[0][currentFrameIndex] = current_frame
            currentFrameIndex++
        }else{
            currentFrameIndex = 0
        }
        return 1
    }

    private operator fun get(
        x: Int,
        y: Int,
        arr: FloatArray
    ): Float {
        return if (x < 0 || y < 0 || x >= outputW || y >= outputH) -1f else arr[x * outputW + y]
    }

    companion object {
        private const val modelFileSize = 16
        /**
         * Create ImageClassifierFloatInception instance
         *
         * @param imageSizeX Get the image size along the x axis.
         * @param imageSizeY Get the image size along the y axis.
         * @param outputW The output width of model
         * @param outputH The output height of model
         * @param modelPath Get the name of the model file stored in Assets.
         * @param actionPath Get the name of the action model file stored in Assets.
         * @param numBytesPerChannel Get the number of bytes that is used to store a single
         * color channel value.
         */
        fun create(
            activity: Activity,
            imageSizeX: Int = 192,
            imageSizeY: Int = 192,
            outputW: Int = 96,
            outputH: Int = 96,
            modelPath: String = "humanposemodel.tflite",
            actionPath: String = "model" + modelFileSize + ".tflite",
            numBytesPerChannel: Int = 4
        ): ImageClassifierFloatInception =
            ImageClassifierFloatInception(
                activity,
                imageSizeX,
                imageSizeY,
                outputW,
                outputH,
                modelPath,
                actionPath,
                numBytesPerChannel)
    }
}
