package com.example.takimoto.camera_api_sample.presentation.fragment

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.ImageReader
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.util.Size
import android.view.*
import android.widget.Button
import com.example.takimoto.camera_api_sample.R
import com.example.takimoto.camera_api_sample.data.ImageFileStore
import com.example.takimoto.camera_api_sample.domain.thread.CameraBackgroundThread
import com.example.takimoto.camera_api_sample.domain.usecase.CameraInterface
import com.example.takimoto.camera_api_sample.domain.usecase.CameraUseCase
import com.example.takimoto.camera_api_sample.domain.usecase.CompareSizesByArea
import com.example.takimoto.camera_api_sample.presentation.view.dialog.PermissionSettingDialog
import com.example.takimoto.camera_api_sample.presentation.view.view.AutoFitTextureView
import com.example.takimoto.camera_api_sample.util.PermissoinUtil
import java.io.File
import java.util.*

/**
 * カメラ機能
 * Camera2 API(Android M以上用)
 *
 * Created by takimoto on 2017/12/25.
 */
class CameraFragment : Fragment(), CameraInterface {

    private var mIsPermissionAlreadyDenied = false

    private lateinit var mCameraUseCase: CameraUseCase

    private lateinit var mTextureView: AutoFitTextureView

    private lateinit var mPreviewSize: Size

    private lateinit var mCameraBackgroundThread: CameraBackgroundThread

    private var mImageReader: ImageReader? = null

    private val mSurfaceTextureListener = object : TextureView.SurfaceTextureListener {

        override fun onSurfaceTextureAvailable(texture: SurfaceTexture, width: Int, height: Int) {
            // SurfaceTextureの準備が完了した
            openCamera(width, height)
        }

        override fun onSurfaceTextureSizeChanged(texture: SurfaceTexture, width: Int, height: Int) {
            // Viewのサイズに変更があったためPreviewサイズを計算し直す
//            configurePreviewTransform(width, height)
        }

        override fun onSurfaceTextureDestroyed(texture: SurfaceTexture): Boolean {
            return true
        }

        override fun onSurfaceTextureUpdated(texture: SurfaceTexture) {}
    }

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

        mCameraUseCase = CameraUseCase().also {
            it.setInterface(this)
        }

        mCameraBackgroundThread = CameraBackgroundThread()
    }

    override fun onResume() {
        super.onResume()
        mCameraBackgroundThread.start()

        // スクリーンがOFFにされ後に再度ONにすると、TextureViewがすでに使用可能になっており、
        // TextureView.SurfaceTextureListener.onSurfaceTextureAvailable()が呼び出されないため
        // その場合は、すぐカメラプレビューを実行する
        if (mTextureView.isAvailable) {
            openCamera(mTextureView.width, mTextureView.height)
        } else {
            mTextureView.surfaceTextureListener = mSurfaceTextureListener
        }
    }

    override fun onPause() {
        super.onPause()
        closeCamera()
        mCameraBackgroundThread.stop()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        if (requestCode == PermissoinUtil.CAMERA_REQUEST_CODE) {
            if (grantResults.size != 1 || !PermissoinUtil.hasGranted(grantResults[0])) {
                mIsPermissionAlreadyDenied = true
                showSettingDialog()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun showSettingDialog() {
        val dialog = PermissionSettingDialog()
        val fragmentTransaction = childFragmentManager.beginTransaction().apply {
            add(dialog, null)
        }
        fragmentTransaction.commitAllowingStateLoss()
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun openCamera(width: Int, height: Int) {
        if (!PermissoinUtil.checkSelfPermission(activity, Manifest.permission.CAMERA)) {
            requestPermission()
            return
        }

        val cameraId = getCameraId() ?: return

        setUpCameraOutputs(cameraId)
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun getCameraId(): String? {
        val manager = activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            for (cameraId in manager.cameraIdList) {

                val cameraCharacteristics = manager.getCameraCharacteristics(cameraId)

                // フロントカメラを利用しない
                val cameraDirection = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)
                if (cameraDirection != null
                        && cameraDirection == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue
                }

                // ストリーム制御をサポートしていない場合、セットアップを中断する
                cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP) ?: continue

                return cameraId
            }

        } catch (e: CameraAccessException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            // Camera2 API未サポート
            Log.e(TAG, "Camera Error:not support Camera2API")
        }

        return null
    }

    /**
     * カメラの出力セットアップ
     */
    @TargetApi(Build.VERSION_CODES.M)
    private fun setUpCameraOutputs(cameraId: String) {
        val manager = activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val map = manager.getCameraCharacteristics(cameraId)
                    .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

            // 最大サイズでキャプチャする
            val largestSize = Collections.max(
                    Arrays.asList(*map.getOutputSizes(ImageFormat.JPEG)),
                    CompareSizesByArea())

            mImageReader = ImageReader.newInstance(largestSize.width, largestSize.height,
                    ImageFormat.JPEG, 2).apply {
                setOnImageAvailableListener(
                        { reader ->
                            // 画像を保存
                            val fileName = "pic_" + System.currentTimeMillis() + ".jpg"
                            val file = File(activity.getExternalFilesDir(null), fileName)
                            mCameraBackgroundThread.getHandler()?.post(ImageFileStore(reader.acquireNextImage(), file))

                        }, mCameraBackgroundThread.getHandler()
                )
            }

//            setUpPreview(map.getOutputSizes(SurfaceTexture::class.java),
//                    width, height, largest)
//            configurePreviewTransform(width, height)

        } catch (e: CameraAccessException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            // Camera2 API未サポート
            Log.e(TAG, "Camera Error:not support Camera2API")
        }
    }

    private fun requestPermission() {
        if (!mIsPermissionAlreadyDenied) {
            PermissoinUtil.requestPermission(this, arrayOf(Manifest.permission.CAMERA), PermissoinUtil.CAMERA_REQUEST_CODE)
        } else {
            mIsPermissionAlreadyDenied = false
        }
    }

    private fun closeCamera() {
//        mCameraUseCase.close()
        mImageReader?.close()
        mImageReader = null
    }

    override val surfaceTextureFromTextureView: SurfaceTexture = mTextureView.surfaceTexture

    override val previewSize: Size = mPreviewSize

    override val backgroundHandler: Handler? = mCameraBackgroundThread.getHandler()

    override val imageRenderSurface: Surface? = mImageReader?.surface

    override val rotation: Int = activity.windowManager.defaultDisplay.rotation

    companion object {
        private val TAG = this::class.java.simpleName
    }
}