package api.luisangeldd.mediapicker.h.data

import android.net.Uri
import api.luisangeldd.mediapicker.h.data.repository.MediaPickerRepo
import javax.inject.Inject

class MediaPickerUseCase  @Inject constructor (private val mediaPickerRepo: MediaPickerRepo) {
    suspend fun fetchMedia() = mediaPickerRepo.fetchMedia()
    fun fetchThumbnail(uri : Uri, id : Long, mimeType : String) = mediaPickerRepo.fetchThumbnail(uri, id, mimeType)
}