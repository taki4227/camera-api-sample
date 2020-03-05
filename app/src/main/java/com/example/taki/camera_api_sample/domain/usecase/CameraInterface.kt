package com.example.taki.camera_api_sample.domain.usecase

import android.graphics.SurfaceTexture
import android.os.Handler
import android.util.Size
import android.view.Surface

/**
 * Created by taki on 2017/12/27.
 */
interface CameraInterface {
    val surfaceTextureFromTextureView: SurfaceTexture
    val previewSize: Size
    val backgroundHandler: Handler?
    val imageRenderSurface: Surface?
    val rotation: Int
}