package com.example.takimoto.camera_api_sample.data

import android.media.Image
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by takimoto on 2017/12/27.
 */
class ImageFileStore(private val image: Image, private val file: File) : Runnable {

    private val logTag = this::class.java.simpleName

    override fun run() {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        var output: FileOutputStream? = null
        try {
            output = FileOutputStream(file).apply {
                write(bytes)
            }
        } catch (e: IOException) {
            Log.e(logTag, e.toString())
        } finally {
            image.close()
            output?.let {
                try {
                    it.close()
                } catch (e: IOException) {
                    Log.e(logTag, e.toString())
                }
            }
        }
    }

}