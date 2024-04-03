package api.luisangeldd.mediapicker.data.model

import android.net.Uri
import java.io.File
data class MediaData(
    val idMedia: Long,
    val uriMedia: Uri,
    val fileMedia: File,
    val mimeType: String
)
data class AlbumData(
    val itemsFolder: Int,
    val pathFromFolder: File,
    val uri: Uri,
    val id : Long,
    val mimeType : String,
)
