package api.luisangeldd.mediapicker.data

import android.net.Uri
import api.luisangeldd.mediapicker.data.repository.MediaPickerRepo

class MediaPickerUseCase(private val mediaPickerRepo: MediaPickerRepo) {
    suspend fun fetchMedia() = mediaPickerRepo.fetchMedia()
    fun fetchThumbnail(uri : Uri, id : Long, mimeType : String) = mediaPickerRepo.fetchThumbnail(uri, id, mimeType)
}