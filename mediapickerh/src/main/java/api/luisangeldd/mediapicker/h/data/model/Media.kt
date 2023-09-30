package api.luisangeldd.mediapicker.h.data.model

import android.net.Uri
import java.io.File

data class Media(
    val idMedia: Long,
    val uriMedia: Uri,
    val fileMedia: File,
    val mimeType: String
)

