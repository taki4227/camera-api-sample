package com.example.takimoto.camera_api_sample.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.takimoto.camera_api_sample.fragment.CameraFragment

/**
 * Created by takimoto on 2017/12/15.
 */
class CameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, CameraFragment())
                .commitAllowingStateLoss()
    }
}
