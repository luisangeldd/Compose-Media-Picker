package api.luisangeldd.mediapicker.data

import android.net.Uri
import api.luisangeldd.mediapicker.data.repository.MediaPickerRepo

internal class MediaPickerUseCase(private val mediaPickerRepo: MediaPickerRepo) {
    suspend fun fetchMedia() = mediaPickerRepo.fetchMedia()
    suspend fun fetchThumbnail(
        uri : Uri,
        id : Long,
        mimeType : String,
        resolutionHeight: Boolean
    ) = mediaPickerRepo.fetchThumbnail(
        uri,
        id,
        mimeType,
        resolutionHeight
    )
}