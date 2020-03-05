package com.example.taki.camera_api_sample.util

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.PermissionChecker

/**
 * Created by taki on 2017/12/26.
 */
class PermissoinUtil {

    companion object {
        const val CAMERA_REQUEST_CODE = 1

        fun checkSelfPermission(activity: Activity, permissions: Array<String>): Boolean {
            return permissions.none { checkSelfPermission(activity, it) }
        }

        fun checkSelfPermission(activity: Activity, permission: String): Boolean {
            return hasGranted(PermissionChecker.checkSelfPermission(activity, permission))
        }

        fun requestPermission(activity: Activity, permissions: Array<String>, requestCode: Int) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(activity, permissions, requestCode)
            }
        }

        fun requestPermission(fragment: Fragment, permissions: Array<String>, requestCode: Int) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fragment.requestPermissions(permissions, requestCode)
            }
        }

        fun hasGranted(grantResults: Array<Int>): Boolean {
            return grantResults.none { hasGranted(it) }
        }

        fun hasGranted(grantResult: Int): Boolean {
            return grantResult == PackageManager.PERMISSION_GRANTED
        }
    }
}