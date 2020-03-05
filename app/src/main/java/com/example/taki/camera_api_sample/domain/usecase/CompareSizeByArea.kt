package com.example.taki.camera_api_sample.domain.usecase

import android.os.Build
import android.util.Size
import java.lang.Long.signum
import java.util.*

/**
 * Compares two `Size`s based on their areas.
 * Created by taki on 2017/12/27.
 */
internal class CompareSizesByArea : Comparator<Size> {

    override fun compare(lhs: Size, rhs: Size): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // We cast here to ensure the multiplications won't overflow
            return signum(lhs.width.toLong() * lhs.height - rhs.width.toLong() * rhs.height)
        }
        return 0
    }
}

