package com.example.takimoto.camera_api_sample.fragment

import android.Manifest
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.takimoto.camera_api_sample.R
import com.example.takimoto.camera_api_sample.util.PermissoinUtil
import com.example.takimoto.camera_api_sample.view.dialog.PermissionSettingDialog

/**
 * Created by takimoto on 2017/12/25.
 */
class CameraFragment : Fragment() {

    private var isPermissionAlreadyDenied = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity.findViewById<Button>(R.id.permission_setting_button)
                .setOnClickListener {
                    PermissoinUtil.requestPermission(this, arrayOf(Manifest.permission.CAMERA), PermissoinUtil.CAMERA_REQUEST_CODE)
                }
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
                isPermissionAlreadyDenied = true
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
        if (!isPermissionAlreadyDenied) {
            PermissoinUtil.requestPermission(this, arrayOf(Manifest.permission.CAMERA), PermissoinUtil.CAMERA_REQUEST_CODE)
        } else {
            isPermissionAlreadyDenied = false
        }
    }

    private fun showSettingDialog() {
        val dialog = PermissionSettingDialog()
        val fragmentTransaction = childFragmentManager.beginTransaction().apply {
            add(dialog, null)
        }
        fragmentTransaction.commitAllowingStateLoss()
    }
}