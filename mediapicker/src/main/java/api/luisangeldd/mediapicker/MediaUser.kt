package api.luisangeldd.mediapicker

import android.net.Uri
import java.io.File

data class MediaUser (
    val uriMedia: Uri,
    val fileMedia: File
)