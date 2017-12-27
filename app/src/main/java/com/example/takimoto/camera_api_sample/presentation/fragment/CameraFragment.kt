package com.example.takimoto.camera_api_sample.presentation.fragment

import android.Manifest
import android.graphics.SurfaceTexture
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Size
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.takimoto.camera_api_sample.R
import com.example.takimoto.camera_api_sample.domain.thread.CameraBackgroundThread
import com.example.takimoto.camera_api_sample.domain.usecase.CameraInterface
import com.example.takimoto.camera_api_sample.domain.usecase.CameraUseCase
import com.example.takimoto.camera_api_sample.presentation.view.dialog.PermissionSettingDialog
import com.example.takimoto.camera_api_sample.presentation.view.view.AutoFitTextureView
import com.example.takimoto.camera_api_sample.util.PermissoinUtil

/**
 * Created by takimoto on 2017/12/25.
 */
class CameraFragment : Fragment(), CameraInterface {

    private val logTag = this::class.java.simpleName

    private var mIsPermissionAlreadyDenied = false

    private lateinit var mCameraUseCase: CameraUseCase

    private lateinit var mTextureView: AutoFitTextureView

    private lateinit var mPreviewSize: Size

    private lateinit var mCameraBackgroundThread: CameraBackgroundThread

    private var imageReader: ImageReader? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mTextureView = view.findViewById(R.id.texture_view)

        activity.findViewById<Button>(R.id.permission_setting_button)
                .setOnClickListener {
                    PermissoinUtil.requestPermission(this, arrayOf(Manifest.permission.CAMERA), PermissoinUtil.CAMERA_REQUEST_CODE)
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCameraUseCase = CameraUseCase()

        mCameraBackgroundThread = CameraBackgroundThread()
    }

    override fun onResume() {
        super.onResume()

        openCamera()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        if (requestCode == PermissoinUtil.CAMERA_REQUEST_CODE) {
            if (grantResults.size > 1 && PermissoinUtil.hasGranted(grantResults[0])) {
                Toast.makeText(activity, "Accept permission", Toast.LENGTH_LONG).show()
            } else {
                mIsPermissionAlreadyDenied = true
                Toast.makeText(activity, "Deny permission", Toast.LENGTH_LONG).show()
                showSettingDialog()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun openCamera() {
        if (!PermissoinUtil.checkSelfPermission(activity, Manifest.permission.CAMERA)) {
            requestPermission()
            return
        }
        Toast.makeText(activity, "Open camera", Toast.LENGTH_LONG).show()
    }

    private fun requestPermission() {
        if (!mIsPermissionAlreadyDenied) {
            PermissoinUtil.requestPermission(this, arrayOf(Manifest.permission.CAMERA), PermissoinUtil.CAMERA_REQUEST_CODE)
        } else {
            mIsPermissionAlreadyDenied = false
        }
    }

    private fun showSettingDialog() {
        val dialog = PermissionSettingDialog()
        val fragmentTransaction = childFragmentManager.beginTransaction().apply {
            add(dialog, null)
        }
        fragmentTransaction.commitAllowingStateLoss()
    }

    override val surfaceTextureFromTextureView: SurfaceTexture = mTextureView.surfaceTexture

    override val previewSize: Size = mPreviewSize

    override val backgroundHandler: Handler? = mCameraBackgroundThread.getHandler()

    override val imageRenderSurface: Surface? = imageReader?.surface

    override val rotation: Int = activity.windowManager.defaultDisplay.rotation
}