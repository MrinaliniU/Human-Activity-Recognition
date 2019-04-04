package com.dronelab.posewithkotlin

import android.app.Activity
import android.os.Bundle
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader

class CameraActivity : Activity() {

    private val mLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> isOpenCVInit = true
                LoaderCallbackInterface.INCOMPATIBLE_MANAGER_VERSION -> {
                }
                LoaderCallbackInterface.INIT_FAILED -> {
                }
                LoaderCallbackInterface.INSTALL_CANCELED -> {
                }
                LoaderCallbackInterface.MARKET_ERROR -> {
                }
                else -> {
                    super.onManagerConnected(status)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        if (null == savedInstanceState) {
            fragmentManager
                .beginTransaction()
                .replace(R.id.container, Camera2BasicFragment.newInstance())
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback)
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }

    companion object {

        init {
            //        System.loadLibrary("opencv_java");
            System.loadLibrary("opencv_java3")
        }

        @JvmStatic
        var isOpenCVInit = false
    }
}