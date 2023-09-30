package api.luisangeldd.mediapicker.h

import android.net.Uri
import java.io.File

data class MediaUser (
    val uriMedia: Uri,
    val fileMedia: File
)