package api.luisangeldd.mediapicker.data.repository

import android.graphics.Bitmap
import android.net.Uri
import api.luisangeldd.mediapicker.data.model.Media

interface MediaPickerRepo {
    suspend fun fetchMedia(): List<Media>
    fun fetchThumbnail(uri : Uri, id : Long, mimeType : String): Bitmap
}
