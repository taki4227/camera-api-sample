package com.example.taki.camera_api_sample.domain.thread

import android.os.Handler
import android.os.HandlerThread
import android.util.Log

/**
 * Created by taki on 2017/12/27.
 */
class CameraBackgroundThread {

    private var backgroundThread: HandlerThread? = null
    private var backgroundHandler: Handler? = null

    fun getHandler(): Handler? {
        return backgroundHandler
    }

    fun start() {
        backgroundThread = HandlerThread("CameraBackground")
        backgroundThread?.start()
        backgroundHandler = Handler(backgroundThread?.looper)
    }

    fun stop() {
        backgroundThread?.quitSafely()
        try {
            backgroundThread?.join()
            backgroundThread = null
            backgroundHandler = null
        } catch (e: InterruptedException) {
            Log.e(TAG, e.toString())
        }
    }

    companion object {
        private val TAG = this::class.java.simpleName
    }
}