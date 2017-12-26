package com.example.takimoto.camera_api_sample

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.camera_button)
                .setOnClickListener {
                    startActivity(Intent(this, CameraActivity::class.java))
                }
    }
}
