package com.example.taki.camera_api_sample.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.example.taki.camera_api_sample.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.camera_button)
                .setOnClickListener {
                    startActivity(Intent(this, CameraActivity::class.java))
                }
    }

    companion object {
        private val TAG = this::class.java.simpleName
    }
}
