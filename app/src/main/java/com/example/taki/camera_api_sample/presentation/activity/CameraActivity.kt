package com.example.taki.camera_api_sample.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.taki.camera_api_sample.presentation.fragment.CameraFragment

/**
 * Created by taki on 2017/12/15.
 */
class CameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, CameraFragment())
            .commitAllowingStateLoss()
    }

    companion object {
        private val TAG = this::class.java.simpleName
    }
}
