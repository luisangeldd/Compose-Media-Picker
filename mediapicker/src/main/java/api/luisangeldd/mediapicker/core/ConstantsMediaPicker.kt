package api.luisangeldd.mediapicker.core

import android.Manifest
import android.os.Build

object ConstantsMediaPicker {
    const val mimeImage = "image"
    const val mimeVideo = "video"
    val imageExtensionsSupport = arrayOf("jpg", "jpeg", "png", "gif", "bmp", "webp")
    val videoExtensionsSupport = arrayOf("mp4", "mkv", "avi", "wmv", "mov", "webm")
    /*
        private val imageExtensions = arrayOf("$IMAGE/jpg", "$IMAGE/jpeg", "$IMAGE/png", "$IMAGE/gif", "$IMAGE/bmp", "$IMAGE/webp")
        private val videoExtensions = arrayOf("$VIDEO/mp4", "$VIDEO/mkv", "$VIDEO/avi", "$VIDEO/wmv", "$VIDEO/mov", "$VIDEO/webm")
        private val fileExtensions = imageExtensions.plus(videoExtensions)
    */
    val permissionsToRequest =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
}