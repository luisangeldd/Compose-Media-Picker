package api.luisangeldd.mediapicker.data.repository.hilt

import android.net.Uri

class HiltMediaPickerUseCase(
    private val hiltMediaPickerRepo: HiltMediaPickerRepo
) {
    suspend fun fetchMedia() = hiltMediaPickerRepo.fetchMedia()
    fun fetchThumbnail(uri : Uri, id : Long, mimeType : String) = hiltMediaPickerRepo.fetchThumbnail(uri, id, mimeType)
}