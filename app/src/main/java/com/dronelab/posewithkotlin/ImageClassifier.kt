package com.dronelab.posewithkotlin

import android.app.Activity
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.lang.Long
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*

abstract class ImageClassifier
/** Initializes an `ImageClassifier`.  */
@Throws(IOException::class)
internal constructor(
    activity: Activity,
    val imageSizeX: Int, // Get the image size along the x axis.
    val imageSizeY: Int, // Get the image size along the y axis.
    private val modelPath: String, // Get the name of the model file stored in Assets.
    private val actionPath: String,
    // Get the number of bytes that is used to store a single color channel value.
    numBytesPerChannel: Int
) {

    /* Preallocated buffers for storing image data in. */
    private val intValues = IntArray(imageSizeX * imageSizeY)

    /** An instance of the driver class to run model inference with Tensorflow Lite.  */
    protected var tflite: Interpreter? = null
    protected var tfaction: Interpreter? = null /** TFlite file for action recognition **/

    /** A ByteBuffer to hold image data, to be feed into Tensorflow Lite as inputs.  */
    protected var imgData: ByteBuffer? = null

    /** multi-stage low pass filter *  */
    private var filterLabelProbArray: Array<FloatArray>? = null

    var mPrintPointArray: Array<FloatArray>? = null

    private val sortedLabels = PriorityQueue<Map.Entry<String, Float>>(
        RESULTS_TO_SHOW,
        Comparator<Map.Entry<String, Float>> { o1, o2 -> o1.value.compareTo(o2.value) })


    init {
        tflite = Interpreter(loadModelFile(activity))
        tfaction = Interpreter(loadActionModelFile(activity))
        imgData = ByteBuffer.allocateDirect(
            DIM_BATCH_SIZE
                    * imageSizeX
                    * imageSizeY
                    * DIM_PIXEL_SIZE
                    * numBytesPerChannel
        )
        imgData!!.order(ByteOrder.nativeOrder())
        Log.d(TAG, "Created a Tensorflow Lite Image Classifier.")
    }

    /** Classifies a frame from the preview stream.  */
    fun classifyFrame(bitmap: Bitmap): String {

        if (tflite == null) {
            Log.e(TAG, "Image classifier has not been initialized; Skipped.")
            return "Uninitialized Classifier."
        }
        convertBitmapToByteBuffer(bitmap)
        // Human Pose File
        val startTime = SystemClock.uptimeMillis()
        runInference()
        val endTime = SystemClock.uptimeMillis()
        // Action Model File
        val actionStartTime = SystemClock.uptimeMillis()
        // run new interface here
        var classifiedAction = runActionInference()
        val actionEndTime = SystemClock.uptimeMillis()

        Log.d(TAG, "Timecost to run Pose model inference: " + Long.toString(endTime - startTime)+"ms")
        //Log.d(TAG, "Timecost to run Action model inference: " + Long.toString(actionEndTime - actionStartTime) + "ms")
        //return Long.toString(endTime - startTime) + "ms, action: " + classifiedAction
        return "Action: " + classifiedAction
    }


    /** Closes tflite to release resources.  */
    fun close() {
        tflite!!.close()
        tfaction!!.close()
        tflite = null
        tfaction = null
    }

    /** Memory-map the model file in Assets.  */
    @Throws(IOException::class)
    private fun loadModelFile(activity: Activity): MappedByteBuffer {
        val fileDescriptor = activity.assets.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }


    /** Memory-map the action model file in Assets.  */
    @Throws(IOException::class)
    private fun loadActionModelFile(activity: Activity): MappedByteBuffer {
        val fileDescriptor = activity.assets.openFd(actionPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    /** Writes Image data into a `ByteBuffer`.  */
    private fun convertBitmapToByteBuffer(bitmap: Bitmap) {
        if (imgData == null) {
            return
        }
        imgData!!.rewind()
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        // Convert the image to floating point.
        var pixel = 0
        val startTime = SystemClock.uptimeMillis()
        for (i in 0 until imageSizeX) {
            for (j in 0 until imageSizeY) {
                val v = intValues[pixel++]
                addPixelValue(v)
            }
        }
        val endTime = SystemClock.uptimeMillis()
        Log.d(
            TAG,
            "Timecost to put values into ByteBuffer: " + Long.toString(endTime - startTime)
        )
    }

    /**
     * Add pixelValue to byteBuffer.
     *
     * @param pixelValue
     */
    protected abstract fun addPixelValue(pixelValue: Int)

    /**
     * Read the probability value for the specified label This is either the original value as it was
     * read from the net's output or the updated value after the filter was applied.
     *
     * @param labelIndex
     * @return
     */
    protected abstract fun getProbability(labelIndex: Int): Float

    /**
     * Set the probability value for the specified label.
     *
     * @param labelIndex
     * @param value
     */
    protected abstract fun setProbability(
        labelIndex: Int,
        value: Number
    )

    /**
     * Get the normalized probability value for the specified label. This is the final value as it
     * will be shown to the user.
     *
     * @return
     */
    protected abstract fun getNormalizedProbability(labelIndex: Int): Float

    /**
     * Run inference using the prepared input in [.imgData]. Afterwards, the result will be
     * provided by getProbability().
     *
     *
     * This additional method is necessary, because we don't have a common base for different
     * primitive data types.
     */
    protected abstract fun runInference() : Int

    protected abstract fun runActionInference() : String

    companion object {

        /** Tag for the [Log].  */
        private const val TAG = "TfLiteCameraDemo"

        /** Number of results to show in the UI.  */
        private const val RESULTS_TO_SHOW = 3

        /** Dimensions of inputs.  */
        private const val DIM_BATCH_SIZE = 1

        private const val DIM_PIXEL_SIZE = 3

        private const val FILTER_STAGES = 3
        private const val FILTER_FACTOR = 0.4f
    }
}
