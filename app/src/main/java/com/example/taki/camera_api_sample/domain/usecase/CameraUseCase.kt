package com.example.taki.camera_api_sample.domain.usecase

import android.util.SparseIntArray
import android.view.Surface

/**
 * Created by taki on 2017/12/27.
 */
class CameraUseCase {

    private var mInterface: CameraInterface? = null

    fun setInterface(param: CameraInterface) {
        mInterface = param
    }

    companion object {

        private val TAG = this::class.java.simpleName

        private val ORIENTATIONS = SparseIntArray()
        init {
            ORIENTATIONS.append(Surface.ROTATION_0, 90)
            ORIENTATIONS.append(Surface.ROTATION_90, 0)
            ORIENTATIONS.append(Surface.ROTATION_180, 270)
            ORIENTATIONS.append(Surface.ROTATION_270, 180)
        }
    }
}