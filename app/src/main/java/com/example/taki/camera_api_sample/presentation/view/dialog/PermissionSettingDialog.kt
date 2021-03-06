package com.example.taki.camera_api_sample.presentation.view.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.DialogFragment
import com.example.taki.camera_api_sample.R

/**
 * Created by taki on 2017/12/26.
 */
class PermissionSettingDialog : DialogFragment() {

    companion object {
        const val DIALOG = "PermissionSettingDialog"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(activity)
            .setMessage(R.string.camera_permission_description)
            .setPositiveButton(
                R.string.dialog_permission_setting
            ) { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", activity!!.packageName, null)
                }
                activity!!.startActivity(intent)
            }
            .setNegativeButton(R.string.dialog_cancel, null)
            .create()
}