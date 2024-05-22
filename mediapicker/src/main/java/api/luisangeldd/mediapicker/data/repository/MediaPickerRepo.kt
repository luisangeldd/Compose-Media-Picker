package api.luisangeldd.mediapicker.data.repository

import android.graphics.Bitmap
import android.net.Uri
import api.luisangeldd.mediapicker.data.model.AlbumData
import api.luisangeldd.mediapicker.data.model.MediaData

interface MediaPickerRepo {
    suspend fun fetchMedia(): List<MediaData>
    suspend fun fetchAlbums(files: List<MediaData>): List<AlbumData>
    suspend fun fetchMediaByAlbum(folder: String): List<MediaData>
    suspend fun fetchThumbnail(
        uri : Uri,
        id : Long,
        mimeType : String,
        resolutionHeight: Boolean = true
    ): Bitmap
}
