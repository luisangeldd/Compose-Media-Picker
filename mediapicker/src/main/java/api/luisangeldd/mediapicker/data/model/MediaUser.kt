package api.luisangeldd.mediapicker.data.model

import android.net.Uri
import java.io.File

data class MediaUser (
    val item : Int,
    val uriMedia: Uri,
    val fileMedia: File
)