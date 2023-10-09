package api.luisangeldd.mediapicker.data.repository.koin

import android.net.Uri

class KoinMediaPickerUseCase(private val koinMediaPickerRepo: KoinMediaPickerRepo) {
    suspend fun fetchMedia() = koinMediaPickerRepo.fetchMedia()
    fun fetchThumbnail(uri : Uri, id : Long, mimeType : String) = koinMediaPickerRepo.fetchThumbnail(uri, id, mimeType)
}