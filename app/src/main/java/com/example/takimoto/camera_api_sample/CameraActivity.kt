package com.example.takimoto.camera_api_sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

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
